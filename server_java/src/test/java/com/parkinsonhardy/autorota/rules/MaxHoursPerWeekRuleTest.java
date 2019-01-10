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

import java.util.List;

import static java.time.DayOfWeek.MONDAY;

public class MaxHoursPerWeekRuleTest extends RotaEngineTestBase {

    @Before
    public void setUp() {
        rotaEngine = super.getRotaEngine();
    }

    @Test
    public void testMaxHoursPerWeekRuleAssignsShift() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(6));
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(1, employees.size());
        Assert.assertEquals("Real Ben", employees.get(0).getName());
        Assert.assertEquals(1, employees.get(0).getShifts().size());
        Assert.assertEquals(DateTime.now().withTime(LocalTime.parse("10:30")), employees.get(0).getShifts().get(0).getStartTime());
        Assert.assertEquals(DateTime.now().withTime(LocalTime.parse("15:30")), employees.get(0).getShifts().get(0).getEndTime());
        Assert.assertEquals("Day", employees.get(0).getShifts().get(0).getShiftType());
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleAssignsShiftThrows() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30"));
        rotaEngine.addShiftDefinition(dayShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(5));

        DateTime monday = getNextMonday();
        addShiftToSingleEmployee(dayShift, monday);


        rotaEngine.assignShifts(monday.plusDays(1), monday.plusDays(2));
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleAssignsShiftThrowsAddedInPast() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30"));
        rotaEngine.addShiftDefinition(dayShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(5));

        DateTime monday = getNextMonday();
        addShiftToSingleEmployee(dayShift, monday.plusDays(2));

        rotaEngine.assignShifts(monday, monday.plusDays(1));
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleAssignsShiftThrowsAddedInMiddle() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30"));
        rotaEngine.addShiftDefinition(dayShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(10));

        DateTime monday = getNextMonday();
        addShiftToSingleEmployee(dayShift, monday);
        addShiftToSingleEmployee(dayShift, monday.plusDays(3));

        rotaEngine.assignShifts(monday.plusDays(1), monday.plusDays(2));
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleThrowsExceptionWithLongShift() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(4));
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleThrowsExceptionWithMultipleShifts() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("12:30")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(4));
        DateTime monday = getNextMonday();
        DateTime threeDaysLater = monday.plusDays(3);
        rotaEngine.assignShifts(monday, threeDaysLater);
    }

    @Test
    public void testMaxHoursPerWeekRuleDoesntThrowExceptionWithMultipleShiftsOverWeekThreshold() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("12:30")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(4));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime threeDaysLater = sunday.plusDays(3);
        rotaEngine.assignShifts(sunday, threeDaysLater);

        List<Employee> employees = rotaEngine.getEmployees();
        checkSingleEmployeeHasShiftCount(employees, 3);
    }

    @Test
    public void testMaxHoursPerWeekRuleDoesntThrowExceptionWithMultipleShiftsOverWeekThresholdNightShifts() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("23:00"), LocalTime.parse("01:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(3));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime twoDaysLater = sunday.plusDays(2);
        rotaEngine.assignShifts(sunday, twoDaysLater);

        List<Employee> employees = rotaEngine.getEmployees();
        checkSingleEmployeeHasShiftCount(employees, 2);
    }

    @Test
    public void testMaxHoursPerWeekRuleDoesntThrowExceptionWithSingleShiftOverWeekThresholdNightShifts() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("22:00"), LocalTime.parse("01:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(2));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime oneDayLater = sunday.plusDays(1);
        rotaEngine.assignShifts(sunday, oneDayLater);

        List<Employee> employees = rotaEngine.getEmployees();
        checkSingleEmployeeHasShiftCount(employees, 1);
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleThrowsExceptionWithSingleShiftOverWeekThresholdNightShifts() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("22:00"), LocalTime.parse("01:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(3));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime twoDaysLater = sunday.plusDays(2);
        rotaEngine.assignShifts(sunday, twoDaysLater);
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleThrowsExceptionWithMultipleShiftsOverWeekThresholdNightShifts2() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("22:00"), LocalTime.parse("01:00")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("12:00"), LocalTime.parse("15:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, MONDAY));
        rotaEngine.addRule(new MaxHoursPerWeekRule(3));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime twoDaysLater = sunday.plusDays(2);
        rotaEngine.assignShifts(sunday, twoDaysLater);
    }

    @Test
    public void testMaxHoursPerWeekPassesShiftOverWeekThresholdLastHourPasses() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("23:00"), LocalTime.parse("02:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(21));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime monday = sunday.plusDays(8);
        rotaEngine.assignShifts(sunday, monday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 8);
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekBreaksShiftOverWeekThresholdLastHourBreaks() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("23:00"), LocalTime.parse("02:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(20));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime monday = sunday.plusDays(8);
        rotaEngine.assignShifts(sunday, monday);
    }
}
