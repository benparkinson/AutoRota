package com.parkinsonhardy.autorota.engine;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Period;

public class ShiftHelper {

    public static int CalculateShiftHours(Shift shift) {
        Period difference = new Period(shift.getStartTime(), shift.getEndTime());
        return difference.getHours();
    }

    public static int CalculateShiftHours(LocalTime startTime, LocalTime endTime) {
        Period difference;
        if (startTime.isBefore(endTime)) {
            difference = new Period(startTime, endTime);
        } else {
            return CalculateShiftHours(DateTime.now().withTime(startTime),
                    DateTime.now().plusDays(1).withTime(endTime));
        }

        return difference.getHours();
    }

    public static int CalculateShiftHours(DateTime startTime, DateTime endTime) {
        Duration difference;
        if (startTime.isBefore(endTime)) {
            difference = new Duration(startTime, endTime);
        } else {
            difference = new Duration(DateTime.now().withTime(startTime.toLocalTime()),
                    DateTime.now().plusDays(1).withTime(endTime.toLocalTime()));
        }

        return Math.toIntExact(difference.getStandardHours());
    }
}
