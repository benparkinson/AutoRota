package com.parkinsonhardy.autorota.helpers;

import com.parkinsonhardy.autorota.engine.Shift;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;

public class ShiftHelperTest {

    @Test
    public void testShiftDifference() {
        Shift shift = new Shift("Test", DateTime.parse("2018-01-01T01:00:00Z"),
                DateTime.parse("2018-01-01T05:00:00Z"));
        int i = ShiftHelper.CalculateShiftHours(shift);
        Assert.assertEquals(4, i);
    }

    @Test
    public void testShiftDifferenceOvernight() {
        Shift shift = new Shift("Test", DateTime.parse("2018-01-01T23:00:00Z"),
                DateTime.parse("2018-01-02T05:00:00Z"));
        int i = ShiftHelper.CalculateShiftHours(shift);
        Assert.assertEquals(6, i);
    }

    @Test
    public void testLocalTimeDifference() {
        int i = ShiftHelper.CalculateShiftHours(LocalTime.parse("01:00:00"),
                LocalTime.parse("05:00:00"));
        Assert.assertEquals(4, i);
    }

    @Test
    public void testLocalTimeDifferenceOvernight() {
        int i = ShiftHelper.CalculateShiftHours(LocalTime.parse("23:00:00"),
                LocalTime.parse("05:00:00"));
        Assert.assertEquals(6, i);
    }

    @Test
    public void testDateTimeDifference() {
        int i = ShiftHelper.CalculateShiftHours(DateTime.parse("2018-01-01T01:00:00Z"),
                DateTime.parse("2018-01-01T05:00:00Z"));
        Assert.assertEquals(4, i);
    }

    @Test
    public void testDateTimeDifferenceOvernight() {
        int i = ShiftHelper.CalculateShiftHours(DateTime.parse("2018-01-01T23:00:00Z"),
                DateTime.parse("2018-01-02T05:00:00Z"));
        Assert.assertEquals(6, i);
    }

}
