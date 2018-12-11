package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.engine.*;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.WEDNESDAY;

public class MinHoursBetweenShiftsRuleTest extends RotaEngineTestBase {

    @Before
    public void setUp() {
        rotaEngine = new RotaEngine();
    }

    @Test
    public void testMinHoursBetweenShiftsRule() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("11:30"), LocalTime.parse("12:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day3", LocalTime.parse("12:30"), LocalTime.parse("13:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day4", LocalTime.parse("13:30"), LocalTime.parse("14:30")));
        addTwoEmployees();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        addShiftRequirementForEveryDay(rotaEngine, "Day2", 1);
        addShiftRequirementForEveryDay(rotaEngine, "Day3", 1);
        addShiftRequirementForEveryDay(rotaEngine, "Day4", 1);
        rotaEngine.addRule(new MinHoursBetweenShiftsRule(1));
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());

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

    @Test(expected = RotaException.class)
    public void testMinHoursRuleInPastThrows() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        rotaEngine.addShiftDefinition(dayShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MinHoursBetweenShiftsRule(25));

        DateTime tuesday = getNextMonday().plusDays(1);

        addShiftToSingleEmployee(dayShift, tuesday);

        rotaEngine.assignShifts(getNextMonday(), tuesday);
    }

    @Test
    public void testMinHoursRuleInPastPasses() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        rotaEngine.addShiftDefinition(dayShift);
        ShiftDefinition nightShift = new ShiftDefinition("Night", LocalTime.parse("18:30"), LocalTime.parse("03:30"));
        rotaEngine.addShiftDefinition(nightShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MinHoursBetweenShiftsRule(7));

        DateTime tuesday = getNextMonday().plusDays(1);

        addShiftToSingleEmployee(dayShift, tuesday);
        addShiftToSingleEmployee(nightShift, tuesday);

        rotaEngine.assignShifts(getNextMonday(), tuesday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 4);
    }

    @Test
    public void testMinHoursRuleInMiddlePasses() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        rotaEngine.addShiftDefinition(dayShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MinHoursBetweenShiftsRule(10));

        DateTime tuesday = getNextMonday().plusDays(1);
        DateTime thursday = tuesday.plusDays(2);

        addShiftToSingleEmployee(dayShift, tuesday);
        addShiftToSingleEmployee(dayShift, thursday);

        rotaEngine.assignShifts(tuesday.plusDays(1), thursday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 3);
    }

    @Test(expected = RotaException.class)
    public void testMinHoursRuleInMiddleThrows() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        rotaEngine.addShiftDefinition(dayShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MinHoursBetweenShiftsRule(25));

        DateTime tuesday = getNextMonday().plusDays(1);
        DateTime thursday = tuesday.plusDays(2);

        addShiftToSingleEmployee(dayShift, tuesday);
        addShiftToSingleEmployee(dayShift, thursday);

        rotaEngine.assignShifts(tuesday.plusDays(1), thursday);
    }
}
