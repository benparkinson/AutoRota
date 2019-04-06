package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.helpers.ShiftCreator;
import com.parkinsonhardy.autorota.rules.HolisticRule;
import com.parkinsonhardy.autorota.rules.Rule;
import com.parkinsonhardy.autorota.rules.SoftRule;
import org.joda.time.DateTime;

import java.time.DayOfWeek;
import java.util.*;

// First version of RotaEngine, keeping around for posterity but will remove and replace with Planner impl once finalised
public abstract class RotaEngine {

    private List<ShiftRequirement> shiftRequirements = new ArrayList<>();
    private Map<String, ShiftDefinition> shiftDefinitionsByType = new HashMap<>();
    private List<HolisticRule> holisticRules = new ArrayList<>();
    protected List<Employee> employees = new ArrayList<>();
    protected List<Rule> rules = new ArrayList<>();
    protected List<SoftRule> softRules = new ArrayList<>();
    protected int timeoutInSeconds;

    private int shiftId = 0;

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

    public void addSoftRule(SoftRule rule) {
        this.softRules.add(rule);
    }

    public void addHolisticRule(HolisticRule rule) {
        this.holisticRules.add(rule);
    }

    protected List<Shift> createShifts(DateTime from, DateTime to) throws RotaException {
        List<Shift> allShifts = new ArrayList<>();

        for (DateTime dt = from.withTimeAtStartOfDay();
             dt.isBefore(to.withTimeAtStartOfDay().getMillis());
             dt = dt.plusDays(1)) {
            for (ShiftRequirement shiftRequirement : shiftRequirements) {
                DayOfWeek shiftDay = DayOfWeek.of(dt.getDayOfWeek());
                if (!shiftRequirement.shiftRequiredOnDay(shiftDay)) {
                    continue;
                }

                ShiftDefinition shiftDefinition = shiftDefinitionsByType.get(shiftRequirement.getShiftType());

                if (shiftDefinition == null) {
                    throw new RotaException(String.format("Could not find shift definition for shift type: %s", shiftRequirement.getShiftType()));
                }

                for (int i = 0; i < shiftRequirement.getMinEmployees(); i++) {
                    Shift shift = createShift(dt, shiftDefinition);

                    allShifts.add(shift);
                }
            }
        }
        return allShifts;
    }

    protected Shift createShift(DateTime dt, ShiftDefinition shiftDefinition) {
        return ShiftCreator.createFromDefinition(shiftId++, dt, shiftDefinition);
    }

    public abstract void assignShifts(DateTime from, DateTime to) throws RotaException;

    protected void runPreChecks(DateTime from, DateTime to) throws RotaException {
        for (HolisticRule holisticRule : holisticRules) {
            if (!holisticRule.passesPreCheck(from, to, this.shiftDefinitionsByType, this.shiftRequirements,
                    this.employees)) {
                throw new RotaException(String.format("Holistic rule: %s can not pass given the parameters!", holisticRule.toString()));
            }
        }
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public Set<String> getShiftTypes() {
        return shiftDefinitionsByType.keySet();
    }

    public void setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }
}
