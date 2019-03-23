package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.engine.Employee;
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

    @Test
    public void testMaxHoursPerWeekRuleAssignsShiftThrows() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30"));
        Employee employee = new Employee("Real Ben");

        MaxHoursPerWeekRule rule = new MaxHoursPerWeekRule(5);

        DateTime monday = getNextMonday();
        addShiftToEmployee(dayShift, monday, employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, monday.plusDays(1), employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testMaxHoursPerWeekRuleAssignsShiftThrowsAddedInPast() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30"));
        Employee employee = new Employee("Real Ben");
        MaxHoursPerWeekRule rule = new MaxHoursPerWeekRule(5);

        DateTime monday = getNextMonday();
        addShiftToEmployee(dayShift, monday.plusDays(2), employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, monday, employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testMaxHoursPerWeekRuleAssignsShiftThrowsAddedInMiddle() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30"));
        Employee employee = new Employee("Real Ben");
        MaxHoursPerWeekRule rule = new MaxHoursPerWeekRule(10);

        DateTime monday = getNextMonday();
        addShiftToEmployee(dayShift, monday, employee);
        addShiftToEmployee(dayShift, monday.plusDays(3), employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, monday.plusDays(1), employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
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

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleThrowsExceptionWithMultipleShiftsOverWeekThreshold() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("12:30")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(4));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime threeDaysLater = sunday.plusDays(3);
        rotaEngine.assignShifts(sunday, threeDaysLater);
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleThrowsExceptionWithMultipleShiftsOverWeekThresholdNightShifts() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("23:00"), LocalTime.parse("01:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(3));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime twoDaysLater = sunday.plusDays(2);
        rotaEngine.assignShifts(sunday, twoDaysLater);
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleThrowsExceptionWithSingleShiftOverWeekThresholdNightShifts1() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("22:00"), LocalTime.parse("01:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(2));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime oneDayLater = sunday.plusDays(1);
        rotaEngine.assignShifts(sunday, oneDayLater);
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

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekThrowsShiftOverWeekThresholdLastHourThrows() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("23:00"), LocalTime.parse("02:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        rotaEngine.addRule(new MaxHoursPerWeekRule(20));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime monday = sunday.plusDays(8);
        rotaEngine.assignShifts(sunday, monday);
    }

    @Test
    public void testMaxHoursPerWeekThrowsShiftOverWeekThresholdLastHourPasses() throws RotaException {
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

    @Test
    public void testManyHoursPerWeek() {
        ShiftDefinition longDay = new ShiftDefinition("LongDay", LocalTime.parse("08:30"), LocalTime.parse("21:00"));
        ShiftDefinition night = new ShiftDefinition("Night", LocalTime.parse("20:30"), LocalTime.parse("09:00"));

        Employee employee = new Employee("Real Ben");

        MaxHoursPerWeekRule rule = new MaxHoursPerWeekRule(72);

        DateTime monday = getNextMonday();
        addShiftToEmployee(longDay, monday, employee);
        addShiftToEmployee(longDay, monday.plusDays(1), employee);
        addShiftToEmployee(longDay, monday.plusDays(2), employee);
        addShiftToEmployee(night, monday.plusDays(3), employee);
        addShiftToEmployee(night, monday.plusDays(4), employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(night, monday.plusDays(5), employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }
}
