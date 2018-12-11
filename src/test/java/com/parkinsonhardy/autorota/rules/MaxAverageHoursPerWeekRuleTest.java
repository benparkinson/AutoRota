package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.engine.RotaEngine;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

public class MaxAverageHoursPerWeekRuleTest extends RotaEngineTestBase {

    @Before
    public void setUp() {
        rotaEngine = new RotaEngine();
    }

    @Test
    public void testMaxAverageHoursPerWeekPreCheckPasses() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addHolisticRule(new MaxAverageHoursPerWeekRule(6));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime oneDayLater = sunday.plusDays(1);
        rotaEngine.assignShifts(sunday, oneDayLater);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 1);
    }

    @Test(expected = RotaException.class)
    public void testMaxAverageHoursPerWeekPreCheckThrows() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("15:30")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Day", 1);
        rotaEngine.addHolisticRule(new MaxAverageHoursPerWeekRule(6));
        DateTime sunday = getNextMonday().minusDays(1);
        DateTime threeDaysLater = sunday.plusDays(3);
        rotaEngine.assignShifts(sunday, threeDaysLater);
    }

    @Test
    public void testMaxAverageHoursPerWeekPreCheckPassesShiftOverWeekThreshold() throws RotaException {
        rotaEngine.addShiftDefinition(new ShiftDefinition("Night", LocalTime.parse("22:00"), LocalTime.parse("02:00")));
        addSingleEmployee();
        addShiftRequirementForEveryDay(rotaEngine, "Night", 1);
        rotaEngine.addHolisticRule(new MaxAverageHoursPerWeekRule(6));
        DateTime saturday = getNextMonday().minusDays(2);
        DateTime monday = saturday.plusDays(3);
        rotaEngine.assignShifts(saturday, monday);

        checkSingleEmployeeHasShiftCount(rotaEngine.getEmployees(), 3);
    }

}
