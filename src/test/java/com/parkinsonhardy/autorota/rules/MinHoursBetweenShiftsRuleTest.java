package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import com.parkinsonhardy.autorota.engine.ShiftRequirement;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static java.time.DayOfWeek.*;

public class MinHoursBetweenShiftsRuleTest extends RotaEngineTestBase {

    @Before
    public void setUp() {
        rotaEngine = super.getRotaEngine();
    }

    @Test
    @Ignore // doesn't seem to be consistently applying rules in a short amount of time...
    public void testMinHoursBetweenShiftsRule() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("11:30"), LocalTime.parse("12:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day3", LocalTime.parse("12:30"), LocalTime.parse("13:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day4", LocalTime.parse("13:30"), LocalTime.parse("14:30")));
        addTwoEmployees();
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, MONDAY));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day2", 1, TUESDAY));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day3", 1, WEDNESDAY));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day4", 1, THURSDAY));
        rotaEngine.addRule(new MinHoursBetweenShiftsRule(25));
        rotaEngine.assignShifts(getNextMonday(), getNextMonday().plusDays(4));

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(2, employees.size());
        for (Employee employee : employees) {
            Assert.assertEquals(2, employee.getShifts().size());

            boolean hasDay = false, hasDay2 = false, hasDay3 = false, hasDay4 = false;
            for (Shift shift : employee.getShifts()) {
                if (shift.getShiftType().equals("Day")) {
                    hasDay = true;
                }
                if (shift.getShiftType().equals("Day2")) {
                    hasDay2 = true;
                }
                if (shift.getShiftType().equals("Day3")) {
                    hasDay3 = true;
                }
                if (shift.getShiftType().equals("Day4")) {
                    hasDay4 = true;
                }
            }
            Assert.assertTrue((hasDay && hasDay3) ^ (hasDay2 && hasDay4));
        }
    }

    @Test(expected = RotaException.class)
    public void testMinHoursBetweenShiftsRuleThrowsException() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("11:30"), LocalTime.parse("12:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day3", LocalTime.parse("12:30"), LocalTime.parse("13:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day4", LocalTime.parse("13:30"), LocalTime.parse("14:30")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        addShiftRequirementForEveryDay(rotaEngine, "Day2", 1);
        addShiftRequirementForEveryDay(rotaEngine, "Day3", 1);
        addShiftRequirementForEveryDay(rotaEngine, "Day4", 1);
        rotaEngine.addRule(new MinHoursBetweenShiftsRule(1));
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());
    }

    @Test
    public void testMinHoursBetweenShiftsRulePassesMultipleDays() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("12:30"), LocalTime.parse("13:30")));
        addSingleEmployee();
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, MONDAY));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day2", 1, WEDNESDAY));
        rotaEngine.addRule(new MinHoursBetweenShiftsRule(2));
        DateTime monday = getNextMonday();
        DateTime wednesday = monday.plusDays(3);
        rotaEngine.assignShifts(monday, wednesday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 2);
    }

    @Test
    public void testMinHoursRuleInPastThrows() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        Employee employee = new Employee("Real Ben");
        MinHoursBetweenShiftsRule rule = new MinHoursBetweenShiftsRule(25);
        DateTime monday = getNextMonday();
        DateTime tuesday = monday.plusDays(1);

        addShiftToEmployee(dayShift, tuesday, employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, monday, employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testMinHoursRuleInPastPasses() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        ShiftDefinition nightShift = new ShiftDefinition("Night", LocalTime.parse("18:30"), LocalTime.parse("03:30"));
        Employee employee = new Employee("Real Ben");

        MinHoursBetweenShiftsRule rule = new MinHoursBetweenShiftsRule(7);

        DateTime monday = getNextMonday();
        DateTime tuesday = monday.plusDays(1);

        addShiftToEmployee(dayShift, tuesday, employee);
        addShiftToEmployee(nightShift, tuesday, employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, monday, employee);
        addShiftToEmployee(nightShift, monday, employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testMinHoursRuleInMiddlePasses() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        Employee employee = new Employee("Real Ben");

        MinHoursBetweenShiftsRule rule = new MinHoursBetweenShiftsRule(10);

        DateTime tuesday = getNextMonday().plusDays(1);
        DateTime thursday = tuesday.plusDays(2);

        addShiftToEmployee(dayShift, tuesday, employee);
        addShiftToEmployee(dayShift, thursday, employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, tuesday.plusDays(1), employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testMinHoursRuleInMiddleThrows() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        Employee employee = new Employee("Real Ben");

        MinHoursBetweenShiftsRule rule = new MinHoursBetweenShiftsRule(25);

        DateTime tuesday = getNextMonday().plusDays(1);
        DateTime thursday = tuesday.plusDays(2);

        addShiftToEmployee(dayShift, tuesday, employee);
        addShiftToEmployee(dayShift, thursday, employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, tuesday.plusDays(1), employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }
}
