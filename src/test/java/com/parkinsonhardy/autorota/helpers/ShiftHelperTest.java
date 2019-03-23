package com.parkinsonhardy.autorota.helpers;

import com.parkinsonhardy.autorota.engine.Shift;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;

public class ShiftHelperTest {

    @Test
    public void testShiftDifference() {
        Shift shift = new Shift(0, "Test", DateTime.parse("2018-01-01T01:00:00Z"),
                DateTime.parse("2018-01-01T05:00:00Z"));
        float i = ShiftHelper.calculateShiftHours(shift);
        Assert.assertEquals(4, i, 0);
    }

    @Test
    public void testShiftDifferenceHalfHour() {
        Shift shift = new Shift(0, "Test", DateTime.parse("2018-01-01T01:00:00Z"),
                DateTime.parse("2018-01-01T05:30:00Z"));
        float i = ShiftHelper.calculateShiftHours(shift);
        Assert.assertEquals(4.5, i, 0);
    }

    @Test
    public void testShiftDifferenceOvernight() {
        Shift shift = new Shift(0, "Test", DateTime.parse("2018-01-01T23:00:00Z"),
                DateTime.parse("2018-01-02T05:00:00Z"));
        float i = ShiftHelper.calculateShiftHours(shift);
        Assert.assertEquals(6, i, 0);
    }

    @Test
    public void testLocalTimeDifference() {
        float i = ShiftHelper.calculateShiftHours(LocalTime.parse("01:00:00"),
                LocalTime.parse("05:00:00"));
        Assert.assertEquals(4, i, 0);
    }

    @Test
    public void testLocalTimeDifferenceHalfHour() {
        float i = ShiftHelper.calculateShiftHours(LocalTime.parse("01:00:00"),
                LocalTime.parse("05:30:00"));
        Assert.assertEquals(4.5, i, 0);
    }

    @Test
    public void testLocalTimeDifferenceOvernight() {
        float i = ShiftHelper.calculateShiftHours(LocalTime.parse("23:00:00"),
                LocalTime.parse("05:00:00"));
        Assert.assertEquals(6, i, 0);
    }

    @Test
    public void testDateTimeDifference() {
        float i = ShiftHelper.calculateShiftHours(DateTime.parse("2018-01-01T01:00:00Z"),
                DateTime.parse("2018-01-01T05:00:00Z"));
        Assert.assertEquals(4, i, 0);
    }

    @Test
    public void testDateTimeDifferenceOvernight() {
        float i = ShiftHelper.calculateShiftHours(DateTime.parse("2018-01-01T23:00:00Z"),
                DateTime.parse("2018-01-02T05:00:00Z"));
        Assert.assertEquals(6, i, 0);
    }

    @Test
    public void testDateTimeDifferenceHalfHour() {
        float i = ShiftHelper.calculateShiftHours(DateTime.parse("2018-01-01T23:00:00Z"),
                DateTime.parse("2018-01-02T05:30:00Z"));
        Assert.assertEquals(6.5, i, 0);
    }

}
