package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.rules.Rule;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RotaEngine {

    private Map<String, ShiftDefinition> shiftDefinitionsByType = new HashMap<>();
    private List<ShiftRequirement> shiftRequirements = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();
    private List<Rule> rules = new ArrayList<>();

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

    public void assignShifts(DateTime from, DateTime to) throws RotaException {
        if (0 == shiftRequirements.size()) {
            return;
        }

        for (DateTime dt = from.withTimeAtStartOfDay(); dt.isBefore(to.withTimeAtStartOfDay().getMillis()); dt = dt.plusDays(1)) {
            for (ShiftRequirement shiftRequirement : shiftRequirements) {
                if (shiftRequirement.getDayOfWeek() != dt.getDayOfWeek()) {
                    continue;
                }
                ShiftDefinition shiftDefinition = shiftDefinitionsByType.get(shiftRequirement.getShiftType());
                for (int i = 0; i < shiftRequirement.getMinEmployees(); i++) {
                    Shift shift = new Shift(shiftDefinition.getShiftType(),
                            dt.withTime(shiftDefinition.getStartTime()), dt.withTime(shiftDefinition.getEndTime()));
                    Employee employee = getAvailableEmployee(shift);

                    employee.addShift(shift);
                }
            }
        }
    }

    private Employee getAvailableEmployee(Shift shift) throws RotaException {
        // todo randomise order
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
