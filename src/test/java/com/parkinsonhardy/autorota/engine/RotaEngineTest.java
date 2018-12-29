package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.helpers.IntegerMatcher;
import com.parkinsonhardy.autorota.rules.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RotaEngineTest extends RotaEngineTestBase {

    @Before
    public void setUp() {
        rotaEngine = new ExperimentalRotaEngine();
    }

    @Test
    public void testWorkerGetsAShift() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(1, employees.size());
        Assert.assertEquals(1, employees.get(0).getShifts().size());
        Assert.assertEquals(DateTime.now().withTime(LocalTime.parse("10:30")), employees.get(0).getShifts().get(0).getStartTime());
        Assert.assertEquals(DateTime.now().withTime(LocalTime.parse("11:30")), employees.get(0).getShifts().get(0).getEndTime());
        Assert.assertEquals("Day", employees.get(0).getShifts().get(0).getShiftType());
    }

    @Test
    public void testWorkerGetsAShiftNightShift() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("20:30"), LocalTime.parse("09:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(1, employees.size());
        Assert.assertEquals(1, employees.get(0).getShifts().size());
        Assert.assertEquals(DateTime.now().withTime(LocalTime.parse("20:30")), employees.get(0).getShifts().get(0).getStartTime());
        Assert.assertEquals(DateTime.now().plusDays(1).withTime(LocalTime.parse("09:00")), employees.get(0).getShifts().get(0).getEndTime());
        Assert.assertEquals("Night", employees.get(0).getShifts().get(0).getShiftType());
    }

    @Test
    public void testNoShiftRequirements() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        addSingleEmployee();
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(1, employees.size());
        Assert.assertEquals(0, employees.get(0).getShifts().size());
    }

    @Test(expected = RotaException.class)
    public void testNotEnoughWorkersExceptionThrown() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 2);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());
    }

    @Test
    public void testWorkerGetsAShiftTwoShiftsTwoWorkers() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("LaterDay", LocalTime.parse("11:00"), LocalTime.parse("12:00")));
        addTwoEmployees();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        addShiftRequirementForEveryDay(rotaEngine, "LaterDay", 1);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(2, employees.size());
        for (Employee employee : employees) {
            Assert.assertEquals(1, employee.getShifts().size());
        }
    }

    @Test
    public void testWorkerGetsAShiftTwoDays() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(2).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(1, employees.size());
        for (Employee employee : employees) {
            Assert.assertEquals(2, employee.getShifts().size());
        }
    }

    @Test
    public void testWorkerGetsAShiftTwoShiftsTwoWorkersTwoDays() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("LaterDay", LocalTime.parse("11:00"), LocalTime.parse("12:00")));
        addTwoEmployees();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        addShiftRequirementForEveryDay(rotaEngine, "LaterDay", 1);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(2).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(2, employees.size());
        for (Employee employee : employees) {
            Assert.assertEquals(2, employee.getShifts().size());
        }
    }

    @Test
    public void testWorkerGetsAShiftAndIsPrioritisedForPreviousShift() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"), true));
        addTwoEmployees();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.assignShifts(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(2).withTimeAtStartOfDay());

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(2, employees.size());
        for (Employee employee : employees) {
            if (employee.getShifts().size() > 0) {
                Assert.assertEquals(2, employee.getShifts().size());
            }
        }
    }

    @Test
    public void testWorkerGetsAShiftAndOtherGetsSecond() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"), false));
        addTwoEmployees();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        DateTime monday = getNextMonday();
        rotaEngine.assignShifts(monday, monday.plusDays(2));

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(2, employees.size());
        for (Employee employee : employees) {
            Assert.assertEquals(1, employee.getShifts().size());
        }
    }

    @Test
    public void testWorkerGetsAShiftAndOtherGetsSecondWeekendShifts() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"), false));
        addTwoEmployees();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        DateTime saturday = getNextMonday().minusDays(2);
        rotaEngine.assignShifts(saturday, saturday.plusDays(2));

        List<Employee> employees = rotaEngine.getEmployees();
        Assert.assertEquals(2, employees.size());
        for (Employee employee : employees) {
            int numberOfShifts = employee.getShifts().size();
            if (numberOfShifts != 0 && numberOfShifts != 2) {
                Assert.fail("Expected employees to either have 0 or 2 shifts!");
            }
        }
    }

    @Test
    public void realTest() throws RotaException {
        DateTimeZone.setDefault(DateTimeZone.UTC);

        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("08:30"), LocalTime.parse("17:00")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("LongDay", LocalTime.parse("08:30"), LocalTime.parse("21:00")));
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("20:30"), LocalTime.parse("09:00"), true));
        rotaEngine.addEmployee(new Employee(1, "Doctor Ben"));
        rotaEngine.addEmployee(new Employee(2, "Doctor Dee"));
        rotaEngine.addEmployee(new Employee(3, "Doctor Suede"));
        rotaEngine.addEmployee(new Employee(4, "Doctor Wilson"));
        rotaEngine.addEmployee(new Employee(5, "Doctor Hercules"));
        rotaEngine.addEmployee(new Employee(6, "Doctor Doctor"));
        rotaEngine.addEmployee(new Employee(7, "Doctor Competent"));
        rotaEngine.addEmployee(new Employee(8, "Doctor Test"));
        rotaEngine.addEmployee(new Employee(9, "Doctor Jones"));
        rotaEngine.addEmployee(new Employee(10, "The Doctor"));
        rotaEngine.addEmployee(new Employee(11, "Doctor Octopus"));
        rotaEngine.addEmployee(new Employee(12, "Doctor Doom"));
//        rotaEngine.addEmployee(new Employee(13, "Doctor 2"));
//        rotaEngine.addEmployee(new Employee(14, "Doctor 3"));
//        rotaEngine.addEmployee(new Employee(15, "Doctor 5"));
//        rotaEngine.addEmployee(new Employee(16, "Doctor 6"));
//        rotaEngine.addEmployee(new Employee(17, "Doctor 7"));
//        rotaEngine.addEmployee(new Employee(18, "Doctor 8"));
//        rotaEngine.addEmployee(new Employee(19, "Doctor 9"));
//        rotaEngine.addEmployee(new Employee(20, "Doctor 10"));
//        rotaEngine.addEmployee(new Employee(21, "Doctor 11"));
//        rotaEngine.addEmployee(new Employee(22, "Doctor 12"));
        addShiftRequirementForEveryDay(rotaEngine, "Night", 2);
        addShiftRequirementForEveryDay(rotaEngine, "LongDay", 2);
        addShiftRequirementForEveryDay(rotaEngine, "Day", 2);
        rotaEngine.addRule(new MinHoursBetweenShiftsRule(11));
        rotaEngine.addRule(new MaxConsecutiveShiftRule("LongDay", 5));
        rotaEngine.addRule(new MaxConsecutiveShiftRule("Night", 4));
        rotaEngine.addRule(new MaxHoursPerWeekRule(72));
        rotaEngine.addRule(new NoMoreThanOneConsecutiveWeekendsRule());
        rotaEngine.addHolisticRule(new MaxAverageHoursPerWeekRule(48));
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("LongDay", 4, 48));
        rotaEngine.addRule(new MinHoursAfterConsecutiveShiftsRule("Night", new IntegerMatcher(3, 4), 46));

        DateTime monday = DateTime.parse("2019-01-07");
        DateTime sixWeeksFromNow = monday.plusWeeks(6);

        try {
            rotaEngine.assignShifts(monday, sixWeeksFromNow);
        } finally {
            printRota(rotaEngine, monday, sixWeeksFromNow);
        }
    }

    // todo test year threshold for consecutive weekends rule
    // todo test max consecutive shift rule with year threshold - use jodatime Duration and get days between probs

    private void printRota(RotaEngine rotaEngine, DateTime startDate, DateTime endDate) {
        StringBuilder sb = new StringBuilder();
        for (Employee employee : rotaEngine.getEmployees()) {
            sb.append(employee.getName()).append("\n");
            for (DateTime dt = startDate; dt.isBefore(endDate.plusDays(1)); dt = dt.plusDays(1)) {
                sb.append(dt.toString("yyyy-MM-dd")).append(",");
            }
            sb.append("\n");

            for (DateTime dt = startDate; dt.isBefore(endDate.plusDays(1)); dt = dt.plusDays(1)) {
                boolean hadShift = false;
                for (Shift shift : employee.getShifts()) {
                    if (shift.getStartTime().withTimeAtStartOfDay().equals(dt)) {
                        sb.append(shift.getShiftType()).append(",");
                        hadShift = true;
                        break;
                    }
                }
                if (!hadShift) {
                    sb.append(",");
                }
            }
            sb.append("\n\n");
        }
        System.out.println(sb.toString());
    }
}
