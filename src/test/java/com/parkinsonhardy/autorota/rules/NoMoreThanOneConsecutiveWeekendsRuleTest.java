package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
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

    @Test(expected = RotaException.class)
    public void testMaxConsecutiveWeekendRuleThrowsAddedInPast() throws RotaException {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30"));
        rotaEngine.addShiftDefinition(dayShift);
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addRule(new NoMoreThanOneConsecutiveWeekendsRule());

        DateTime saturday = getNextMonday().plusDays(5);
        DateTime saturdayInTwoWeeks = saturday.plusWeeks(1);

        addShiftToSingleEmployee(dayShift, saturdayInTwoWeeks);

        rotaEngine.assignShifts(saturday, saturday.plusDays(2));
    }
}