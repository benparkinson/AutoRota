package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.rules.HolisticRule;
import com.parkinsonhardy.autorota.rules.Rule;
import org.joda.time.DateTime;

import java.util.*;

public class RotaEngine {

    private Map<String, ShiftDefinition> shiftDefinitionsByType = new HashMap<>();
    private List<ShiftRequirement> shiftRequirements = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();
    private List<Rule> rules = new ArrayList<>();
    private List<HolisticRule> holisticRules = new ArrayList<>();

    public void addShiftDefinition(ShiftDefinition shiftDefinition) {
        this.shiftDefinitionsByType.put(shiftDefinition.getShiftType(), shiftDefinition);
    }

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
    }

    public void addShiftRequirement(ShiftRequirement shiftRequirement) {
        this.shiftRequirements.add(shiftRequirement);
    }

    public void addRules(Rule rule) {
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

        for (DateTime dt = from.withTimeAtStartOfDay(); dt.isBefore(to.withTimeAtStartOfDay().getMillis()); dt = dt.plusDays(1)) {
            for (HolisticRule holisticRule : holisticRules) {
                holisticRule.interrimCheck(employees);
            }
            for (ShiftRequirement shiftRequirement : shiftRequirements) {
                if (shiftRequirement.getDayOfWeek() != dt.getDayOfWeek()) {
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
                    Employee employee = getAvailableEmployee(shift);

                    employee.addShift(shift);
                }
            }
        }

        for (HolisticRule holisticRule : holisticRules) {
            holisticRule.finalCheck(employees);
        }
    }

    private Employee getAvailableEmployee(Shift shift) throws RotaException {
        employees.sort(Comparator.comparingInt(Employee::getPriorityWeight));
        for (Employee employee : employees) {
            if (employee.isAvailableForShift(shift)) {
                if (shiftIsAcceptable(employee, shift)) {
                    return employee;
                }
            }
        }

        throw new RotaException(String.format("Can't find an available employee for shift! Shift: %s", shift.toString()));
    }

    private boolean shiftIsAcceptable(Employee employee, Shift shift) {
        for (Rule rule : rules) {
            if (!rule.employeeCanWorkShift(employee, shift)) {
                return false;
            }
        }
        return true;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}
