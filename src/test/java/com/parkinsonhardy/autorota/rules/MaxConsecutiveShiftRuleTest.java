package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.RotaEngine;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import com.parkinsonhardy.autorota.engine.ShiftRequirement;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.time.DayOfWeek.*;

public class MaxConsecutiveShiftRuleTest extends RotaEngineTestBase {

    @Before
    public void setUp() {
        rotaEngine = new RotaEngine();
    }

    @Test
    public void testNoTwoDaysInARowRule() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        rotaEngine.addShiftDefinition(dayShift);
        ShiftDefinition laterDayShift = new ShiftDefinition("LaterDay", LocalTime.parse("11:00"), LocalTime.parse("12:00"));
        rotaEngine.addShiftDefinition(laterDayShift);
        addTwoEmployees();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        addShiftRequirementForEveryDay(rotaEngine, "LaterDay", 1);
        rotaEngine.addRule(new MaxConsecutiveShiftRule("Day", 1));
        DateTime today = DateTime.now().withTimeAtStartOfDay();

        Employee employee1 = rotaEngine.getEmployees().get(0);
        Employee employee2 = rotaEngine.getEmployees().get(1);

        addShiftToEmployee(dayShift, today, employee1);
        addShiftToEmployee(laterDayShift, today, employee2);
        rotaEngine.assignShifts(today.plusDays(1), today.plusDays(2));

        Assert.assertEquals(2, employee1.getShifts().size());
        Assert.assertEquals(2, employee2.getShifts().size());

        Assert.assertEquals("LaterDay", employee1.getShifts().get(1).getShiftType());
        Assert.assertEquals("Day", employee2.getShifts().get(1).getShiftType());
    }

    @Test(expected = RotaException.class)
    public void testNoFourDaysInARowRuleThrowsExceptionShiftAtEnd() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        rotaEngine.addShiftDefinition(dayShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MaxConsecutiveShiftRule("Day", 4));

        DateTime today = DateTime.now().withTimeAtStartOfDay();

        addShiftToSingleEmployee(dayShift, today);
        addShiftToSingleEmployee(dayShift, today.plusDays(1));
        addShiftToSingleEmployee(dayShift, today.plusDays(2));
        addShiftToSingleEmployee(dayShift, today.plusDays(3));

        rotaEngine.assignShifts(today.plusDays(4), today.plusDays(5));
    }

    @Test(expected = RotaException.class)
    public void testNoFourDaysInARowRuleThrowsExceptionShiftAtStart() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        rotaEngine.addShiftDefinition(dayShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MaxConsecutiveShiftRule("Day", 4));

        DateTime today = DateTime.now().withTimeAtStartOfDay();

        addShiftToSingleEmployee(dayShift, today.plusDays(1));
        addShiftToSingleEmployee(dayShift, today.plusDays(2));
        addShiftToSingleEmployee(dayShift, today.plusDays(3));
        addShiftToSingleEmployee(dayShift, today.plusDays(4));

        rotaEngine.assignShifts(today, today.plusDays(1));
    }

    @Test(expected = RotaException.class)
    public void testNoFourDaysInARowRuleThrowsExceptionShiftAtMiddle() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        rotaEngine.addShiftDefinition(dayShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MaxConsecutiveShiftRule("Day", 4));

        DateTime today = DateTime.now().withTimeAtStartOfDay();

        addShiftToSingleEmployee(dayShift, today.plusDays(1));
        addShiftToSingleEmployee(dayShift, today.plusDays(3));
        addShiftToSingleEmployee(dayShift, today.plusDays(4));
        addShiftToSingleEmployee(dayShift, today.plusDays(5));

        rotaEngine.assignShifts(today.plusDays(2), today.plusDays(3));
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
    public void testMaxConsecutiveShiftRulePassesIfDayBreak3() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        rotaEngine.addShiftDefinition(dayShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MaxConsecutiveShiftRule("Day", 3));

        DateTime today = DateTime.now().withTimeAtStartOfDay();

        addShiftToSingleEmployee(dayShift, today.plusDays(1));
        addShiftToSingleEmployee(dayShift, today.plusDays(3));
        addShiftToSingleEmployee(dayShift, today.plusDays(5));

        rotaEngine.assignShifts(today.plusDays(2), today.plusDays(3));
    }
}
