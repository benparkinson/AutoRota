package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import com.parkinsonhardy.autorota.engine.ShiftRequirement;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.helpers.IntegerMatcher;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.time.DayOfWeek.*;

public class MinHoursAfterConsecutiveShiftsRuleTest extends RotaEngineTestBase {

    @Before
    public void setUp() {
        rotaEngine = super.getRotaEngine();
    }

    @Test
    public void testMinHoursAfterShiftRuleCanAssignMultipleConsecutiveShifts() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Day", 5, 20));
        DateTime monday = getNextMonday();
        DateTime thursday = monday.plusDays(4);
        rotaEngine.assignShifts(monday, thursday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 4);
    }

    @Test(expected = RotaException.class)
    public void testMinHoursAfterShiftRuleThrows() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Day", 5, 50));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);
    }

    @Test(expected = RotaException.class)
    public void testMinHoursAfterShiftRuleThrowsDifferentShiftType() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("15:00"), LocalTime.parse("16:00")));
        addSingleEmployee();
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day2", 1, SATURDAY));
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Day", 5, 50));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);
    }

    @Test
    public void testMinHoursAfterShiftRuleDoesntThrowIfSmallEnoughGapBetweenShifts() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Day", 5, 5));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 6);
    }

    @Test
    public void testMinHoursAfterShiftRuleDoesntThrowIfMultiplePossibleConsecutiveShiftCounts() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Day", new IntegerMatcher(5, 6), 50));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 6);
    }

    @Test(expected = RotaException.class)
    public void testMinHoursAfterShiftRuleThrowsIfMultiplePossibleConsecutiveShiftCounts() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Day", new IntegerMatcher(4, 5), 50));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);
    }

    @Test(expected = RotaException.class)
    public void testMinHoursAfterShiftRuleThrowsDifferentShiftTypeMultipleConsecutiveMatchers() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("15:00"), LocalTime.parse("16:00")));
        addSingleEmployee();
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day2", 1, SATURDAY));
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Day", new IntegerMatcher(5, 6), 50));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);
    }

    @Test
    public void testMinHoursAfterShiftRuleDoesntThrowDifferentShiftTypeMultipleConsecutiveMatchers() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("15:00"), LocalTime.parse("16:00")));
        addSingleEmployee();
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day2", 1, SATURDAY));
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Day", new IntegerMatcher(5, 6), 24));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 6);
    }

    @Test
    public void testMinHoursAfterShiftRuleDoesntThrowDayBreak() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        addSingleEmployee();
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, MONDAY, TUESDAY, THURSDAY, FRIDAY));
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Day", 3, 47));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 4);
    }

    @Test
    public void testMinHoursAfterShiftRuleDoesntThrowDayBreak2() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        addSingleEmployee();
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, MONDAY, TUESDAY, WEDNESDAY, FRIDAY));
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Day", 3, 20));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 4);
    }

    @Test(expected = RotaException.class)
    public void testMinHoursAfterShiftRuleThrows2() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        addSingleEmployee();
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, MONDAY, TUESDAY, WEDNESDAY, FRIDAY));
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Day", 3, 50));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);
    }

    @Test(expected = RotaException.class)
    public void testMinHoursAfterShiftRuleThrows3() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("12:00"), LocalTime.parse("13:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day2", 1);
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Day", 1, 50));
        DateTime monday = getNextMonday();
        DateTime friday = monday.plusDays(5);
        rotaEngine.assignShifts(monday, friday);
    }

    @Test(expected = RotaException.class)
    public void testMinHoursAfterShiftRuleThrows4() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("20:30"), LocalTime.parse("09:00")));
        addSingleEmployee();
        rotaEngine.addShiftRequirement(new ShiftRequirement("Night", 1, MONDAY, TUESDAY, WEDNESDAY, FRIDAY));
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Night", new IntegerMatcher(3, 4), 46));
        DateTime monday = getNextMonday();
        DateTime friday = monday.plusDays(5);
        rotaEngine.assignShifts(monday, friday);
    }

    @Test
    public void testMinHoursAfterShiftRuleThrowsShiftInPast() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30"));
        Employee employee = new Employee("Real Ben");
        MinHoursAfterConsecutiveShiftsRule rule = new MinHoursAfterConsecutiveShiftsRule("Day", 1, 50);

        DateTime monday = getNextMonday();
        DateTime tuesday = monday.plusDays(1);

        addShiftToEmployee(dayShift, tuesday, employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, monday, employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testMinHoursAfterShiftRuleThrowsShiftInPast2() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30"));
        Employee employee = new Employee("Real Ben");
        MinHoursAfterConsecutiveShiftsRule rule = new MinHoursAfterConsecutiveShiftsRule("Day", 2, 50);

        DateTime monday = getNextMonday();
        DateTime tuesday = monday.plusDays(1);
        DateTime wednesday = monday.plusDays(2);

        addShiftToEmployee(dayShift, tuesday, employee);
        addShiftToEmployee(dayShift, wednesday, employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, monday, employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testMinHoursAfterShiftRuleThrowsShiftInMiddle() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30"));
        Employee employee = new Employee("Real Ben");
        MinHoursAfterConsecutiveShiftsRule rule = new MinHoursAfterConsecutiveShiftsRule("Day", 2, 50);

        DateTime monday = getNextMonday();
        DateTime tuesday = monday.plusDays(1);
        DateTime wednesday = monday.plusDays(2);

        addShiftToEmployee(dayShift, wednesday, employee);
        addShiftToEmployee(dayShift, monday, employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, tuesday, employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }
}
