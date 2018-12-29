package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.helpers.ShiftHelper;
import com.parkinsonhardy.autorota.rules.HolisticRule;
import com.parkinsonhardy.autorota.rules.Rule;
import org.joda.time.DateTime;

import java.time.DayOfWeek;
import java.util.*;
import java.util.logging.Logger;

public class RotaEngine {

    private static final Logger logger = Logger.getLogger(RotaEngine.class.getName());

    protected List<ShiftRequirement> shiftRequirements = new ArrayList<>();
    protected List<Employee> employees = new ArrayList<>();
    protected Map<String, ShiftDefinition> shiftDefinitionsByType = new HashMap<>();
    protected List<Rule> rules = new ArrayList<>();
    protected List<HolisticRule> holisticRules = new ArrayList<>();

    public void addShiftDefinition(ShiftDefinition shiftDefinition) {
        this.shiftDefinitionsByType.put(shiftDefinition.getShiftType(), shiftDefinition);
    }

    public void addEmployee(Employee employee) throws RotaException {
        if (employees.contains(employee)) {
            throw new RotaException(String.format("Employee: %s already added!", employee));
        }
        this.employees.add(employee);
    }

    public void addShiftRequirement(ShiftRequirement shiftRequirement) {
        this.shiftRequirements.add(shiftRequirement);
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    public void addHolisticRule(HolisticRule rule) {
        this.holisticRules.add(rule);
    }

    public void assignShifts(DateTime from, DateTime to) throws RotaException {
        if (0 == shiftRequirements.size()) {
            return;
        }

        for (HolisticRule holisticRule : holisticRules) {
            if (!holisticRule.passesPreCheck(from, to, this.shiftDefinitionsByType, this.shiftRequirements,
                    this.employees)) {
                throw new RotaException(String.format("Holistic rule: %s can not pass given the parameters!", holisticRule.toString()));
            }
        }

        for (ShiftRequirement shiftRequirement : shiftRequirements) {
            for (DateTime dt = from.withTimeAtStartOfDay(); dt.isBefore(to.withTimeAtStartOfDay().getMillis()); dt = dt.plusDays(1)) {
                if (!shiftRequirement.shiftRequiredOnDay(DayOfWeek.of(dt.getDayOfWeek()))) {
                    continue;
                }
                ShiftDefinition shiftDefinition = shiftDefinitionsByType.get(shiftRequirement.getShiftType());
                if (shiftDefinition == null) {
                    throw new RotaException(String.format("Could not find shift definition for shift type: %s", shiftRequirement.getShiftType()));
                }
                for (int i = 0; i < shiftRequirement.getMinEmployees(); i++) {
                    DateTime endDate;
                    if (shiftDefinition.getEndTime().isBefore(shiftDefinition.getStartTime())) {
                        endDate = dt.plusDays(1);
                    } else {
                        endDate = dt;
                    }
                    Shift shift = new Shift(shiftDefinition.getShiftType(),
                            dt.withTime(shiftDefinition.getStartTime()), endDate.withTime(shiftDefinition.getEndTime()));
                    List<Employee> employees = getAvailableEmployees(shift);
                    Employee toAssign = null;
                    for (Employee employee : employees) {
                        List<Shift> shiftsCopy = new ArrayList<>(employee.getShifts());
                        shiftsCopy.add(shift);
                        Collections.sort(shiftsCopy);
                        if (shiftIsAcceptable(shiftsCopy)) {
                            toAssign = employee;
                            break;
                        }
                    }

                    if (toAssign == null) {
                        throw new RotaException(String.format("Can't find an available employee for shift! Shift: %s", shift.toString()));
                    }
                    toAssign.addShift(shift);
                }
            }
        }

        for (HolisticRule holisticRule : holisticRules) {
            holisticRule.passesFinalCheck(employees);
        }
    }

    protected List<Employee> getAvailableEmployees(Shift shift) {
        boolean priorityShift = false;
        ShiftDefinition shiftDefinition = shiftDefinitionsByType.get(shift.getShiftType());
        if (shiftDefinition.isAllocateInBlocks() || shift.getStartTime().getDayOfWeek() == DayOfWeek.SUNDAY.getValue()
                || shift.getStartTime().getDayOfWeek() == DayOfWeek.SATURDAY.getValue()) {
            priorityShift = true;
        }
        List<Employee> ret = new ArrayList<>();
        Employee priorityEmployee;
        for (Employee e : employees) {
            boolean isPriority = false;
            if (priorityShift) {
                List<Shift> shiftsForEmployee = e.getShifts();
                int shiftCount = shiftsForEmployee.size();
                if (shiftCount > 0) {
                    Shift previousShift = shiftsForEmployee.get((shiftCount - 1));
                    if (previousShift.getShiftType().equals(shift.getShiftType())
                            && previousShift.getStartTime().withTimeAtStartOfDay().equals(
                            shift.getStartTime().withTimeAtStartOfDay().minusDays(1))) {
                        priorityEmployee = e;
                        isPriority = true;
                        priorityEmployee.setPriorityWeight(Integer.MIN_VALUE);
                    }
                }
            }

            if (!isPriority) {
                int hours = sumEmployeeHours(e);
                e.setPriorityWeight(hours);
            }
        }

        employees.sort(Comparator.comparingInt(Employee::getPriorityWeight));
        for (Employee employee : employees) {
            if (employee.isAvailableForShift(shift)) {
                if (shiftIsOnWeekend(shift) && employeeWorkedConsecutiveWeekend(shift, employee))
                    continue;

                ret.add(employee);
            }
        }

        return ret;
    }

    private boolean employeeWorkedConsecutiveWeekend(Shift shift, Employee employee) {
        int week;
        if (shift.getStartTime().getDayOfWeek() == DayOfWeek.SATURDAY.getValue() ||
        shift.getStartTime().getDayOfWeek() == DayOfWeek.SUNDAY.getValue()) {
            week = shift.getStartTime().getWeekOfWeekyear();
        } else {
            week = shift.getEndTime().getWeekOfWeekyear();
        }

        for (Shift s : employee.getShifts()) {
            if (shiftIsOnWeekend(s)) {
                if (Math.abs(s.getStartTime().getWeekOfWeekyear() - week) == 1)
                    return true;
            }
        }

        return false;
    }

    protected boolean shiftIsOnWeekend(Shift shift) {
        return shift.getStartTime().getDayOfWeek() == DayOfWeek.SUNDAY.getValue()
                || shift.getEndTime().getDayOfWeek() == DayOfWeek.SUNDAY.getValue()
                || shift.getStartTime().getDayOfWeek() == DayOfWeek.SATURDAY.getValue()
                || shift.getEndTime().getDayOfWeek() == DayOfWeek.SATURDAY.getValue();
    }

    private int sumEmployeeHours(Employee employee) {
        int employeeTotalHours = 0;
        for (Shift shift : employee.getShifts()) {
            employeeTotalHours += ShiftHelper.CalculateShiftHours(shift);
        }
        return employeeTotalHours;
    }

    private boolean shiftIsAcceptable(List<Shift> shifts) {
        for (Rule rule : rules) {
            if (!rule.shiftsPassesRule(shifts)) {
                return false;
            }
        }
        return true;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}
