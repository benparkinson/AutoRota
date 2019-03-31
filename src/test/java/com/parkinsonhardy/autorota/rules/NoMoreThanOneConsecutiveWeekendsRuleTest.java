package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.RotaEngine;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NoMoreThanOneConsecutiveWeekendsRuleTest extends RotaEngineTestBase {

    @Before
    public void setUp() {
        rotaEngine = super.getRotaEngine();
    }

    @Test
    public void testMaxConsecutiveWeekendRulePasses() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new NoMoreThanOneConsecutiveWeekendsRule());
        DateTime monday = getNextMonday();
        DateTime oneWeekLater = monday.plusWeeks(1);
        rotaEngine.assignShifts(monday, oneWeekLater);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 7);
    }

    @Test(expected = RotaException.class)
    public void testMaxConsecutiveWeekendRuleThrows() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30"));
        rotaEngine.addShiftDefinition(dayShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new NoMoreThanOneConsecutiveWeekendsRule());

        DateTime saturday = getNextMonday().plusDays(5);

        addShiftToSingleEmployee(dayShift, saturday);

        rotaEngine.assignShifts(saturday.plusDays(1), saturday.plusWeeks(1).plusDays(1));
    }


    @Test
    public void testMaxConsecutiveWeekendRulePasses2() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30")));
        addTwoEmployees();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new NoMoreThanOneConsecutiveWeekendsRule());
        DateTime monday = getNextMonday();
        DateTime threeWeekLater = monday.plusWeeks(3);
        rotaEngine.assignShifts(monday, threeWeekLater);
    }

    @Test
    public void testMaxConsecutiveWeekendRuleThrowsAddedInPast() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30"));
        Employee employee = new Employee("Real Ben");

        NoMoreThanOneConsecutiveWeekendsRule rule = new NoMoreThanOneConsecutiveWeekendsRule();
        rotaEngine.addRule(rule);

        DateTime saturday = getNextMonday().plusDays(5);
        DateTime saturdayInTwoWeeks = saturday.plusWeeks(1);

        addShiftToEmployee(dayShift, saturdayInTwoWeeks, employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, saturday, employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testManyShiftsBug() {
        Employee employee = new Employee("Real Ben");

        employee.addShift(new Shift(16, "Day", DateTime.parse("2019-01-09T08:30:00.000Z"), DateTime.parse("2019-01-09T17:00:00.000Z")));
        employee.addShift(new Shift(28, "Day", DateTime.parse("2019-01-11T08:30:00.000Z"), DateTime.parse("2019-01-11T17:00:00.000Z")));
        employee.addShift(new Shift(34, "Day", DateTime.parse("2019-01-12T08:30:00.000Z"), DateTime.parse("2019-01-12T17:00:00.000Z")));
        employee.addShift(new Shift(40, "Day", DateTime.parse("2019-01-13T08:30:00.000Z"), DateTime.parse("2019-01-13T17:00:00.000Z")));
        employee.addShift(new Shift(52, "Day", DateTime.parse("2019-01-15T08:30:00.000Z"), DateTime.parse("2019-01-15T17:00:00.000Z")));
        employee.addShift(new Shift(65, "Day", DateTime.parse("2019-01-17T08:30:00.000Z"), DateTime.parse("2019-01-17T17:00:00.000Z")));
        employee.addShift(new Shift(68, "LongDay", DateTime.parse("2019-01-18T08:30:00.000Z"), DateTime.parse("2019-01-18T21:00:00.000Z")));
        employee.addShift(new Shift(88, "Day", DateTime.parse("2019-01-21T08:30:00.000Z"), DateTime.parse("2019-01-21T17:00:00.000Z")));
        employee.addShift(new Shift(100, "Day", DateTime.parse("2019-01-23T08:30:00.000Z"), DateTime.parse("2019-01-23T17:00:00.000Z")));
        employee.addShift(new Shift(118, "Day", DateTime.parse("2019-01-26T08:30:00.000Z"), DateTime.parse("2019-01-26T17:00:00.000Z")));
        employee.addShift(new Shift(124, "Day", DateTime.parse("2019-01-27T08:30:00.000Z"), DateTime.parse("2019-01-27T17:00:00.000Z")));
        employee.addShift(new Shift(136, "Day", DateTime.parse("2019-01-29T08:30:00.000Z"), DateTime.parse("2019-01-29T17:00:00.000Z")));
        employee.addShift(new Shift(148, "Day", DateTime.parse("2019-01-31T08:30:00.000Z"), DateTime.parse("2019-01-31T17:00:00.000Z")));
        employee.addShift(new Shift(160, "Day", DateTime.parse("2019-02-02T08:30:00.000Z"), DateTime.parse("2019-02-02T17:00:00.000Z")));
        employee.addShift(new Shift(166, "Day", DateTime.parse("2019-02-03T08:30:00.000Z"), DateTime.parse("2019-02-03T17:00:00.000Z")));
        employee.addShift(new Shift(184, "Day", DateTime.parse("2019-02-06T08:30:00.000Z"), DateTime.parse("2019-02-06T17:00:00.000Z")));
        employee.addShift(new Shift(190, "Day", DateTime.parse("2019-02-07T08:30:00.000Z"), DateTime.parse("2019-02-07T17:00:00.000Z")));
        employee.addShift(new Shift(194, "LongDay", DateTime.parse("2019-02-08T08:30:00.000Z"), DateTime.parse("2019-02-08T21:00:00.000Z")));
        employee.addShift(new Shift(219, "LongDay", DateTime.parse("2019-02-12T08:30:00.000Z"), DateTime.parse("2019-02-12T21:00:00.000Z")));
        employee.addShift(new Shift(226, "Day", DateTime.parse("2019-02-13T08:30:00.000Z"), DateTime.parse("2019-02-13T17:00:00.000Z")));
        employee.addShift(new Shift(236, "LongDay", DateTime.parse("2019-02-15T08:30:00.000Z"), DateTime.parse("2019-02-15T21:00:00.000Z")));
        employee.addShift(new Shift(250, "Day", DateTime.parse("2019-02-17T08:30:00.000Z"), DateTime.parse("2019-02-17T17:00:00.000Z")));
        employee.addShift(new Shift(263, "Day", DateTime.parse("2019-02-19T08:30:00.000Z"), DateTime.parse("2019-02-19T17:00:00.000Z")));
        employee.addShift(new Shift(280, "Day", DateTime.parse("2019-02-22T08:30:00.000Z"), DateTime.parse("2019-02-22T17:00:00.000Z")));
        employee.addShift(new Shift(286, "Day", DateTime.parse("2019-02-23T08:30:00.000Z"), DateTime.parse("2019-02-23T17:00:00.000Z")));
        employee.addShift(new Shift(292, "Day", DateTime.parse("2019-02-24T08:30:00.000Z"), DateTime.parse("2019-02-24T17:00:00.000Z")));
        employee.addShift(new Shift(304, "Day", DateTime.parse("2019-02-26T08:30:00.000Z"), DateTime.parse("2019-02-26T17:00:00.000Z")));
        employee.addShift(new Shift(315, "LongDay", DateTime.parse("2019-02-28T08:30:00.000Z"), DateTime.parse("2019-02-28T21:00:00.000Z")));
        employee.addShift(new Shift(320, "LongDay", DateTime.parse("2019-03-01T08:30:00.000Z"), DateTime.parse("2019-03-01T21:00:00.000Z")));
        employee.addShift(new Shift(339, "LongDay", DateTime.parse("2019-03-04T08:30:00.000Z"), DateTime.parse("2019-03-04T21:00:00.000Z")));
        employee.addShift(new Shift(346, "Day", DateTime.parse("2019-03-05T08:30:00.000Z"), DateTime.parse("2019-03-05T17:00:00.000Z")));
        employee.addShift(new Shift(358, "Day", DateTime.parse("2019-03-07T08:30:00.000Z"), DateTime.parse("2019-03-07T17:00:00.000Z")));
        employee.addShift(new Shift(362, "LongDay", DateTime.parse("2019-03-08T08:30:00.000Z"), DateTime.parse("2019-03-08T21:00:00.000Z")));
        employee.addShift(new Shift(370, "Day", DateTime.parse("2019-03-09T08:30:00.000Z"), DateTime.parse("2019-03-09T17:00:00.000Z")));
        employee.addShift(new Shift(376, "Day", DateTime.parse("2019-03-10T08:30:00.000Z"), DateTime.parse("2019-03-10T17:00:00.000Z")));
        employee.addShift(new Shift(395, "Day", DateTime.parse("2019-03-13T08:30:00.000Z"), DateTime.parse("2019-03-13T17:00:00.000Z")));
        employee.addShift(new Shift(400, "Day", DateTime.parse("2019-03-14T08:30:00.000Z"), DateTime.parse("2019-03-14T17:00:00.000Z")));
        employee.addShift(new Shift(413, "Day", DateTime.parse("2019-03-16T08:30:00.000Z"), DateTime.parse("2019-03-16T17:00:00.000Z")));
        employee.addShift(new Shift(418, "Day", DateTime.parse("2019-03-17T08:30:00.000Z"), DateTime.parse("2019-03-17T17:00:00.000Z")));
        employee.addShift(new Shift(430, "Day", DateTime.parse("2019-03-19T08:30:00.000Z"), DateTime.parse("2019-03-19T17:00:00.000Z")));
        employee.addShift(new Shift(442, "Day", DateTime.parse("2019-03-21T08:30:00.000Z"), DateTime.parse("2019-03-21T17:00:00.000Z")));
        employee.addShift(new Shift(446, "LongDay", DateTime.parse("2019-03-22T08:30:00.000Z"), DateTime.parse("2019-03-22T21:00:00.000Z")));
        employee.addShift(new Shift(466, "Day", DateTime.parse("2019-03-25T08:30:00.000Z"), DateTime.parse("2019-03-25T17:00:00.000Z")));
        employee.addShift(new Shift(472, "Day", DateTime.parse("2019-03-26T08:30:00.000Z"), DateTime.parse("2019-03-26T17:00:00.000Z")));
        employee.addShift(new Shift(479, "Day", DateTime.parse("2019-03-27T08:30:00.000Z"), DateTime.parse("2019-03-27T17:00:00.000Z")));
        employee.addShift(new Shift(485, "Day", DateTime.parse("2019-03-28T08:30:00.000Z"), DateTime.parse("2019-03-28T17:00:00.000Z")));
        employee.addShift(new Shift(488, "LongDay", DateTime.parse("2019-03-29T08:30:00.000Z"), DateTime.parse("2019-03-29T21:00:00.000Z")));
        employee.addShift(new Shift(496, "Day", DateTime.parse("2019-03-30T08:30:00.000Z"), DateTime.parse("2019-03-30T17:00:00.000Z")));
        employee.addShift(new Shift(502, "Day", DateTime.parse("2019-03-31T08:30:00.000Z"), DateTime.parse("2019-03-31T17:00:00.000Z")));

        // should already be sorted, this is just for sanity (and in case the sorting logic of Shifts changes)
        employee.getShifts().sort(Shift::compareTo);

        Assert.assertFalse(new NoMoreThanOneConsecutiveWeekendsRule().shiftsPassesRule(employee.getShifts()));
    }
}