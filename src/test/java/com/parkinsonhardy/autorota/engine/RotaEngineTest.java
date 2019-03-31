package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import com.parkinsonhardy.autorota.helpers.IntegerMatcher;
import com.parkinsonhardy.autorota.rules.*;
import com.parkinsonhardy.autorota.util.RotaUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static java.time.DayOfWeek.*;

public class RotaEngineTest extends RotaEngineTestBase {

    @Before
    public void setUp() {
        rotaEngine = super.getRotaEngine();
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

    // This should be in average hours balance check too
    @Test
    @Ignore
    public void testWorkerGetsAShiftAndIsPrioritisedForPreviousShift() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
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

    // todo this should be in a soft rule test for average hours checks
    @Test
    public void testWorkerGetsAShiftAndOtherGetsSecond() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
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

    // This used to be the RotaEngine's job but should be in a test for average hours balance rule
    @Test
    @Ignore
    public void testWorkerGetsAShiftAndOtherGetsSecondWeekendShifts() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30")));
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
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("20:30"), LocalTime.parse("09:00")));
        rotaEngine.addEmployee(new Employee("Doctor Ben"));
        rotaEngine.addEmployee(new Employee("Doctor Dee"));
        rotaEngine.addEmployee(new Employee("Doctor Suede"));
        rotaEngine.addEmployee(new Employee("Doctor Wilson"));
        rotaEngine.addEmployee(new Employee("Doctor Hercules"));
        rotaEngine.addEmployee(new Employee("Doctor Doctor"));
        rotaEngine.addEmployee(new Employee("Doctor Competent"));
        rotaEngine.addEmployee(new Employee("Doctor Test"));
        rotaEngine.addEmployee(new Employee("Doctor Jones"));
        rotaEngine.addEmployee(new Employee("The Doctor"));
        rotaEngine.addEmployee(new Employee("Doctor Octopus"));
        rotaEngine.addEmployee(new Employee("Doctor Doom"));
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

        rotaEngine.addSoftRule(new AverageHoursBalanceSoftRule(3));
        rotaEngine.addSoftRule(new AvoidSingleShiftsSoftRule(2));
        rotaEngine.addSoftRule(new ShiftBlocksSoftRule(0, new ShiftBlock("Night", FRIDAY, SATURDAY, SUNDAY), true));
        rotaEngine.addSoftRule(new ShiftBlocksSoftRule(0, new ShiftBlock("Night", MONDAY, TUESDAY, WEDNESDAY, THURSDAY), true));
        rotaEngine.addSoftRule(new ShiftBlocksSoftRule(5, new ShiftBlock("LongDay", FRIDAY, SATURDAY, SUNDAY), false));
        rotaEngine.addSoftRule(new ShiftTypeBalanceSoftRule(3, rotaEngine.getShiftTypes()));

        DateTime monday = DateTime.parse("2019-01-07");
        DateTime sixWeeksFromNow = monday.plusWeeks(12);

        try {
            rotaEngine.assignShifts(monday, sixWeeksFromNow);
        } finally {
            String rota = RotaUtils.stringifyRota(rotaEngine.getEmployees(), monday, sixWeeksFromNow);
            System.out.println(rota);
        }
    }
}
