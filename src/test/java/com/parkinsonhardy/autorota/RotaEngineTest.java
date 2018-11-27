package com.parkinsonhardy.autorota;

import com.parkinsonhardy.autorota.engine.*;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.rules.*;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class RotaEngineTest {

    @Test
    public void testWorkerGetsAShift() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addEmployee(new Employee(1, "Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(1, employees.size());
        Assert.assertEquals("Ben", employees.get(0).getName());
        Assert.assertEquals(1, employees.get(0).getShifts().size());
        Assert.assertEquals(DateTime.now().withTime(LocalTime.parse("10:30")), employees.get(0).getShifts().get(0).getStartTime());
        Assert.assertEquals(DateTime.now().withTime(LocalTime.parse("11:30")), employees.get(0).getShifts().get(0).getEndTime());
        Assert.assertEquals("Day", employees.get(0).getShifts().get(0).getShiftType());
    }

    @Test
    public void testWorkerGetsAShiftNightShift() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("20:30"), LocalTime.parse("09:00")));
        rotaEngine.addEmployee(new Employee(1, "Ben"));
        addSimpleRequirement(rotaEngine, "Night", 1);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(1, employees.size());
        Assert.assertEquals("Ben", employees.get(0).getName());
        Assert.assertEquals(1, employees.get(0).getShifts().size());
        Assert.assertEquals(DateTime.now().withTime(LocalTime.parse("20:30")), employees.get(0).getShifts().get(0).getStartTime());
        Assert.assertEquals(DateTime.now().plusDays(1).withTime(LocalTime.parse("09:00")), employees.get(0).getShifts().get(0).getEndTime());
        Assert.assertEquals("Night", employees.get(0).getShifts().get(0).getShiftType());
    }

    @Test
    public void testNoShiftRequirements() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addEmployee(new Employee(1, "Ben"));
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(1, employees.size());
        Assert.assertEquals("Ben", employees.get(0).getName());
        Assert.assertEquals(0, employees.get(0).getShifts().size());
    }

    @Test(expected = RotaException.class)
    public void testNotEnoughWorkersExceptionThrown() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addEmployee(new Employee(1, "Ben"));
        addSimpleRequirement(rotaEngine, "Day", 2);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());
    }

    @Test
    public void testWorkerGetsAShiftTwoShiftsTwoWorkers() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("LaterDay", LocalTime.parse("11:00"), LocalTime.parse("12:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        rotaEngine.addEmployee(new Employee(2, "Shit Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        addSimpleRequirement(rotaEngine, "LaterDay", 1);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(2, employees.size());
        for (Employee employee : employees) {
            Assert.assertEquals(1, employee.getShifts().size());
        }
    }

    @Test
    public void testWorkerGetsAShiftTwoDays() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(2).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(1, employees.size());
        for (Employee employee : employees) {
            Assert.assertEquals(2, employee.getShifts().size());
        }
    }

    @Test
    public void testWorkerGetsAShiftTwoShiftsTwoWorkersTwoDays() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("LaterDay", LocalTime.parse("11:00"), LocalTime.parse("12:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        rotaEngine.addEmployee(new Employee(2, "Shit Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        addSimpleRequirement(rotaEngine, "LaterDay", 1);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(2).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(2, employees.size());
        for (Employee employee : employees) {
            Assert.assertEquals(2, employee.getShifts().size());
        }
    }

    @Test
    public void testNoTwoDaysInARowRule() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("LaterDay", LocalTime.parse("11:00"), LocalTime.parse("12:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        rotaEngine.addEmployee(new Employee(2, "Shit Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        addSimpleRequirement(rotaEngine, "LaterDay", 1);
        rotaEngine.addRules(new MaxConsecutiveShiftRule("Day", 1));
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(2).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(2, employees.size());
        for (Employee employee : employees) {
            Assert.assertEquals(2, employee.getShifts().size());
            boolean hasDayShift = false, hasLaterDayShift = false;
            for (Shift shift : employee.getShifts()) {
                if (shift.getShiftType().equals("Day")) {
                    hasDayShift = true;
                } else if (shift.getShiftType().equals("LaterDay")) {
                    hasLaterDayShift = true;
                }
            }
            if (!hasDayShift || !hasLaterDayShift) {
                Assert.fail(String.format("Employee: %s didn't have all expected shifts!", employee.getName()));
            }
        }
    }

    @Test(expected = RotaException.class)
    public void testNoTwoDaysInARowRuleThrowsException() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addRules(new MaxConsecutiveShiftRule("Day", 4));
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(5).withTimeAtStartOfDay());
    }

    @Test
    public void testMinHoursBetweenShiftsRule() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("11:30"), LocalTime.parse("12:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day3", LocalTime.parse("12:30"), LocalTime.parse("13:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day4", LocalTime.parse("13:30"), LocalTime.parse("14:30")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        rotaEngine.addEmployee(new Employee(2, "Shit Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        addSimpleRequirement(rotaEngine, "Day2", 1);
        addSimpleRequirement(rotaEngine, "Day3", 1);
        addSimpleRequirement(rotaEngine, "Day4", 1);
        rotaEngine.addRules(new MinHoursBetweenShiftsRule(1));
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
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("11:30"), LocalTime.parse("12:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day3", LocalTime.parse("12:30"), LocalTime.parse("13:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day4", LocalTime.parse("13:30"), LocalTime.parse("14:30")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        addSimpleRequirement(rotaEngine, "Day2", 1);
        addSimpleRequirement(rotaEngine, "Day3", 1);
        addSimpleRequirement(rotaEngine, "Day4", 1);
        rotaEngine.addRules(new MinHoursBetweenShiftsRule(1));
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());
    }

    @Test
    public void testMaxHoursPerWeekRuleAssignsShift() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addRules(new MaxHoursPerWeekRule(6));
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
    public void testMaxHoursPerWeekRuleThrowsExceptionWithLongShift() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addRules(new MaxHoursPerWeekRule(4));
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleThrowsExceptionWithMultipleShifts() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("12:30")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addRules(new MaxHoursPerWeekRule(4));
        DateTime monday = getNextMonday();
        DateTime threeDaysLater = monday.plusDays(3);
        rotaEngine.assignShifts(monday, threeDaysLater);
    }

    @Test
    public void testMaxHoursPerWeekRuleDoesntThrowExceptionWithMultipleShiftsOverWeekThreshold() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("12:30")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addRules(new MaxHoursPerWeekRule(4));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime threeDaysLater = sunday.plusDays(3);
        rotaEngine.assignShifts(sunday, threeDaysLater);

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(1, employees.size());
        Assert.assertEquals(3, employees.get(0).getShifts().size());
    }

    @Test
    public void testMaxHoursPerWeekRuleDoesntThrowExceptionWithMultipleShiftsOverWeekThresholdNightShifts() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("23:00"), LocalTime.parse("01:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Night", 1);
        rotaEngine.addRules(new MaxHoursPerWeekRule(3));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime twoDaysLater = sunday.plusDays(2);
        rotaEngine.assignShifts(sunday, twoDaysLater);

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(1, employees.size());
        Assert.assertEquals(2, employees.get(0).getShifts().size());
    }

    @Test
    public void testMaxHoursPerWeekRuleDoesntThrowExceptionWithSingleShiftOverWeekThresholdNightShifts() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("22:00"), LocalTime.parse("01:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Night", 1);
        rotaEngine.addRules(new MaxHoursPerWeekRule(2));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime oneDayLater = sunday.plusDays(1);
        rotaEngine.assignShifts(sunday, oneDayLater);

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(1, employees.size());
        Assert.assertEquals(1, employees.get(0).getShifts().size());
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleThrowsExceptionWithSingleShiftOverWeekThresholdNightShifts() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("22:00"), LocalTime.parse("01:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Night", 1);
        rotaEngine.addRules(new MaxHoursPerWeekRule(3));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime twoDaysLater = sunday.plusDays(2);
        rotaEngine.assignShifts(sunday, twoDaysLater);
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekRuleThrowsExceptionWithMultipleShiftsOverWeekThresholdNightShifts2() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("22:00"), LocalTime.parse("01:00")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("12:00"), LocalTime.parse("15:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Night", 1);
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 1));
        rotaEngine.addRules(new MaxHoursPerWeekRule(3));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime twoDaysLater = sunday.plusDays(2);
        rotaEngine.assignShifts(sunday, twoDaysLater);
    }

    @Test
    public void testMaxAverageHoursPerWeekPreCheckPasses() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addHolisticRule(new MaxAverageHoursPerWeekRule(6));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime oneDayLater = sunday.plusDays(1);
        rotaEngine.assignShifts(sunday, oneDayLater);

        Assert.assertEquals(1, rotaEngine.getEmployees().size());
        Assert.assertEquals(1, rotaEngine.getEmployees().get(0).getShifts().size());
    }

    @Test(expected = RotaException.class)
    public void testMaxAverageHoursPerWeekPreCheckThrows() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addHolisticRule(new MaxAverageHoursPerWeekRule(6));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime threeDaysLater = sunday.plusDays(3);
        rotaEngine.assignShifts(sunday, threeDaysLater);
    }

    @Test
    public void testMaxAverageHoursPerWeekPreCheckPassesShiftOverWeekThreshold() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("22:00"), LocalTime.parse("02:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Night", 1);
        rotaEngine.addHolisticRule(new MaxAverageHoursPerWeekRule(6));
        DateTime saturday = getNextMonday().minusDays(2);
        DateTime monday = saturday.plusDays(3);
        rotaEngine.assignShifts(saturday, monday);

        Assert.assertEquals(1, rotaEngine.getEmployees().size());
        Assert.assertEquals(3, rotaEngine.getEmployees().get(0).getShifts().size());
    }

    @Test
    public void testMaxAverageHoursPerWeekPassesShiftOverWeekThresholdLastHourBreaks() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("23:00"), LocalTime.parse("02:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Night", 1);
        rotaEngine.addRules(new MaxHoursPerWeekRule(21));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime monday = sunday.plusDays(8);
        rotaEngine.assignShifts(sunday, monday);

        Assert.assertEquals(1, rotaEngine.getEmployees().size());
        Assert.assertEquals(8, rotaEngine.getEmployees().get(0).getShifts().size());
    }

    @Test(expected = RotaException.class)
    public void testMaxHoursPerWeekBreaksShiftOverWeekThresholdLastHourBreaks() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("23:00"), LocalTime.parse("02:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Night", 1);
        rotaEngine.addRules(new MaxHoursPerWeekRule(20));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime monday = sunday.plusDays(8);
        rotaEngine.assignShifts(sunday, monday);
    }

    @Test
    public void testWorkerGetsAShiftOneShiftTwoWorkersTwoDaysOneEach() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        rotaEngine.addEmployee(new Employee(2, "Shit Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addHolisticRule(new MaxAverageHoursPerWeekRule(5));
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(2).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(2, employees.size());
        for (Employee employee : employees) {
            Assert.assertEquals(1, employee.getShifts().size());
        }
    }

    @Test
    public void testMinHoursAfterShiftRuleCanAssignMultipleConsecutiveShifts() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addRules(new MinHoursAfterConsecutiveShiftsRule("Day", 5, 20));
        DateTime monday = getNextMonday();
        DateTime thursday = monday.plusDays(4);
        rotaEngine.assignShifts(monday, thursday);

        Assert.assertEquals(1, rotaEngine.getEmployees().size());
        Assert.assertEquals(4, rotaEngine.getEmployees().get(0).getShifts().size());
    }

    @Test(expected = RotaException.class)
    public void testMinHoursAfterShiftRuleThrows() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addRules(new MinHoursAfterConsecutiveShiftsRule("Day", 5, 50));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);
    }

    @Test(expected = RotaException.class)
    public void testMinHoursAfterShiftRuleThrowsDifferentShiftType() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("15:00"), LocalTime.parse("16:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 1));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 2));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 3));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 4));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 5));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day2", 1, 6));
        rotaEngine.addRules(new MinHoursAfterConsecutiveShiftsRule("Day", 5, 50));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);
    }

    @Test
    public void testMinHoursAfterShiftRuleDoesntThrowIfSmallEnoughGapBetweenShifts() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addRules(new MinHoursAfterConsecutiveShiftsRule("Day", 5, 5));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);

        Assert.assertEquals(1, rotaEngine.getEmployees().size());
        Assert.assertEquals(6, rotaEngine.getEmployees().get(0).getShifts().size());
    }

    @Test
    public void testMinHoursAfterShiftRuleDoesntThrowIfMultiplePossibleConsecutiveShiftCounts() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addRules(new MinHoursAfterConsecutiveShiftsRule("Day", new IntegerMatcher(5, 6), 50));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);

        Assert.assertEquals(1, rotaEngine.getEmployees().size());
        Assert.assertEquals(6, rotaEngine.getEmployees().get(0).getShifts().size());
    }

    @Test(expected = RotaException.class)
    public void testMinHoursAfterShiftRuleThrowsIfMultiplePossibleConsecutiveShiftCounts() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        addSimpleRequirement(rotaEngine, "Day", 1);
        rotaEngine.addRules(new MinHoursAfterConsecutiveShiftsRule("Day", new IntegerMatcher(4, 5), 50));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);
    }

    @Test(expected = RotaException.class)
    public void testMinHoursAfterShiftRuleThrowsDifferentShiftTypeMultipleConsecutiveMatchers() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("15:00"), LocalTime.parse("16:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 1));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 2));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 3));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 4));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 5));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day2", 1, 6));
        rotaEngine.addRules(new MinHoursAfterConsecutiveShiftsRule("Day", new IntegerMatcher(5, 6), 50));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);
    }

    @Test
    public void testMinHoursAfterShiftRuleDoesntThrowDifferentShiftTypeMultipleConsecutiveMatchers() throws RotaException {
        RotaEngine rotaEngine = new RotaEngine();

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:00"), LocalTime.parse("11:00")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day2", LocalTime.parse("15:00"), LocalTime.parse("16:00")));
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 1));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 2));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 3));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 4));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day", 1, 5));
        rotaEngine.addShiftRequirement(new ShiftRequirement("Day2", 1, 6));
        rotaEngine.addRules(new MinHoursAfterConsecutiveShiftsRule("Day", new IntegerMatcher(5, 6), 24));
        DateTime monday = getNextMonday();
        DateTime saturday = monday.plusDays(6);
        rotaEngine.assignShifts(monday, saturday);

        Assert.assertEquals(1, rotaEngine.getEmployees().size());
        Assert.assertEquals(6, rotaEngine.getEmployees().get(0).getShifts().size());
    }

    private DateTime getNextMonday() {
        DateTime today = DateTime.now().withTimeAtStartOfDay();
        int dayOfWeek = today.getDayOfWeek();
        return today.plusDays(8 - dayOfWeek);
    }

    private void addSimpleRequirement(RotaEngine rotaEngine, String shiftType, int minEmployees) {
        rotaEngine.addShiftRequirement(new ShiftRequirement(shiftType, minEmployees, 1));
        rotaEngine.addShiftRequirement(new ShiftRequirement(shiftType, minEmployees, 2));
        rotaEngine.addShiftRequirement(new ShiftRequirement(shiftType, minEmployees, 3));
        rotaEngine.addShiftRequirement(new ShiftRequirement(shiftType, minEmployees, 4));
        rotaEngine.addShiftRequirement(new ShiftRequirement(shiftType, minEmployees, 5));
        rotaEngine.addShiftRequirement(new ShiftRequirement(shiftType, minEmployees, 6));
        rotaEngine.addShiftRequirement(new ShiftRequirement(shiftType, minEmployees, 7));
    }
}
