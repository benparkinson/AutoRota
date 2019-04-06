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

import static java.time.DayOfWeek.*;

public class MaxConsecutiveShiftRuleTest extends RotaEngineTestBase {

    @Before
    public void setUp() {
        rotaEngine = super.getRotaEngine();
    }

    //todo this test seems dodgy for some reason...
    @Test
    @Ignore
    public void testNoTwoDaysInARowRule() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        rotaEngine.addShiftDefinition(dayShift);
        ShiftDefinition laterDayShift = new ShiftDefinition("LaterDay", LocalTime.parse("11:00"), LocalTime.parse("12:00"));
        rotaEngine.addShiftDefinition(laterDayShift);
        addTwoEmployees();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        addShiftRequirementForEveryDay(rotaEngine, "LaterDay", 1);
        rotaEngine.addRule(new MaxConsecutiveShiftRule("Day", 1));
        rotaEngine.addRule(new MaxConsecutiveShiftRule("LaterDay", 1));
        DateTime today = DateTime.now().withTimeAtStartOfDay();

        rotaEngine.assignShifts(today, today.plusDays(2));

        for (Employee employee : rotaEngine.getEmployees()) {
            Assert.assertEquals(2, employee.getShifts().size());

            boolean hasDay = false, hasLaterDay = false;
            for (Shift shift : employee.getShifts()) {
                if ("Day".equals(shift.getShiftType())) {
                    hasDay = true;
                } else if ("LaterDay".equals(shift.getShiftType())) {
                    hasLaterDay = true;
                }
            }
            Assert.assertTrue(hasDay);
            Assert.assertTrue(hasLaterDay);
        }
    }

    @Test
    public void testNoFourDaysInARowRuleThrowsExceptionShiftAtEnd() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        Employee employee = new Employee("Real Ben");
        MaxConsecutiveShiftRule rule = new MaxConsecutiveShiftRule("Day", 4);

        DateTime today = DateTime.now().withTimeAtStartOfDay();

        addShiftToEmployee(dayShift, today, employee);
        addShiftToEmployee(dayShift, today.plusDays(1), employee);
        addShiftToEmployee(dayShift, today.plusDays(2), employee);
        addShiftToEmployee(dayShift, today.plusDays(3), employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, today.plusDays(4), employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testNoFourDaysInARowRuleThrowsExceptionShiftAtStart() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        Employee employee = new Employee("Real Ben");
        MaxConsecutiveShiftRule rule = new MaxConsecutiveShiftRule("Day", 4);

        DateTime today = DateTime.now().withTimeAtStartOfDay();

        addShiftToEmployee(dayShift, today.plusDays(1), employee);
        addShiftToEmployee(dayShift, today.plusDays(2), employee);
        addShiftToEmployee(dayShift, today.plusDays(3), employee);
        addShiftToEmployee(dayShift, today.plusDays(4), employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, today, employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testNoFourDaysInARowRuleThrowsExceptionShiftAtMiddle() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        Employee employee = new Employee("Real Ben");
        DateTime today = DateTime.now().withTimeAtStartOfDay();

        addShiftToEmployee(dayShift, today.plusDays(1), employee);
        addShiftToEmployee(dayShift, today.plusDays(3), employee);
        addShiftToEmployee(dayShift, today.plusDays(4), employee);
        addShiftToEmployee(dayShift, today.plusDays(5), employee);

        MaxConsecutiveShiftRule rule = new MaxConsecutiveShiftRule("Day", 4);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, today.plusDays(2), employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testMaxConsecutiveShiftRulePassesIfDayBreak() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        addSingleEmployee();
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, MONDAY, TUESDAY, WEDNESDAY, FRIDAY));
        rotaEngine.addRule(new MaxConsecutiveShiftRule("Day", 3));
        DateTime monday = getNextMonday();
        DateTime friday = monday.plusDays(5);
        rotaEngine.assignShifts(monday, friday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 4);
    }

    @Test
    public void testMaxConsecutiveShiftRulePassesIfDayBreak2() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        addSingleEmployee();
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, MONDAY, TUESDAY, THURSDAY, FRIDAY));
        rotaEngine.addRule(new MaxConsecutiveShiftRule("Day", 2));
        DateTime monday = getNextMonday();
        DateTime friday = monday.plusDays(5);
        rotaEngine.assignShifts(monday, friday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 4);
    }

    @Test
    public void testMaxConsecutiveShiftRulePassesIfDayBreak3() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        Employee employee = new Employee("Real Ben");
        DateTime today = DateTime.now().withTimeAtStartOfDay();
        addShiftToEmployee(dayShift, today.plusDays(1), employee);
        addShiftToEmployee(dayShift, today.plusDays(3), employee);
        addShiftToEmployee(dayShift, today.plusDays(5), employee);

        MaxConsecutiveShiftRule rule = new MaxConsecutiveShiftRule("Day", 3);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, today.plusDays(2), employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testMaxConsecutiveNightShiftsAtWeekend() {
        ShiftDefinition nightShift = new ShiftDefinition("Night", LocalTime.parse("21:00"), LocalTime.parse("09:00"));
        ShiftDefinition testShift = new ShiftDefinition("Test", LocalTime.parse("21:00"), LocalTime.parse("09:00"));
        Employee employee = new Employee("Real Ben");
        MaxConsecutiveShiftRule rule = new MaxConsecutiveShiftRule("Night", 4);
        DateTime nextMonday = getNextMonday();
        DateTime friday = nextMonday.plusDays(5);

        addShiftToEmployee(testShift, friday.minusDays(3), employee);
        addShiftToEmployee(nightShift, friday, employee);
        addShiftToEmployee(nightShift, friday.plusDays(1), employee);
        addShiftToEmployee(nightShift, friday.plusDays(2), employee);
        addShiftToEmployee(nightShift, friday.plusDays(3), employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(nightShift, friday.plusDays(4), employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }

}