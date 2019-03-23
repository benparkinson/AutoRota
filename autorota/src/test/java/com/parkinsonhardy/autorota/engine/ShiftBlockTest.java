package com.parkinsonhardy.autorota.engine;

import org.junit.Test;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class ShiftBlockTest {

    @Test
    public void testShiftBlockCreation() {
        List<DayOfWeek> days = new ArrayList<>();
        days.add(DayOfWeek.FRIDAY);
        days.add(DayOfWeek.SATURDAY);
        days.add(DayOfWeek.SUNDAY);

        ShiftBlock shiftBlock = new ShiftBlock("Test", days);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftBlockCreationExpectException() {
        List<DayOfWeek> days = new ArrayList<>();
        days.add(DayOfWeek.FRIDAY);
        days.add(DayOfWeek.SUNDAY);

        ShiftBlock shiftBlock = new ShiftBlock("Test", days);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftBlockCreationOnlyOneDayExpectException() {
        List<DayOfWeek> days = new ArrayList<>();
        days.add(DayOfWeek.FRIDAY);

        ShiftBlock shiftBlock = new ShiftBlock("Test", days);

    }

}
