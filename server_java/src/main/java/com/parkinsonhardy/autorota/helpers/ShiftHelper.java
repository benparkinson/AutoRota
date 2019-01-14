package com.parkinsonhardy.autorota.helpers;

import com.parkinsonhardy.autorota.engine.Shift;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Period;

public class ShiftHelper {

    public static float CalculateShiftHours(Shift shift) {
        Period difference = new Period(shift.getStartTime(), shift.getEndTime());
        int v = difference.getMinutes() + (difference.getHours() * 60);
        return v / 60f;
    }

    public static float CalculateShiftHours(LocalTime startTime, LocalTime endTime) {
        Period difference;
        if (startTime.isBefore(endTime)) {
            difference = new Period(startTime, endTime);
        } else {
            return CalculateShiftHours(DateTime.now().withTime(startTime),
                    DateTime.now().plusDays(1).withTime(endTime));
        }

        return (difference.getMinutes() + (difference.getHours() * 60)) / 60f;
    }

    public static float CalculateShiftHours(DateTime startTime, DateTime endTime) {
        Duration difference;
        if (startTime.equals(endTime)) {
            return 0;
        }

        difference = new Duration(startTime, endTime);
        return difference.getStandardMinutes() / 60f;
    }
}
