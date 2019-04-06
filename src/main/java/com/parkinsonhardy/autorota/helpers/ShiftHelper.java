package com.parkinsonhardy.autorota.helpers;

import com.parkinsonhardy.autorota.engine.Shift;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.time.DayOfWeek;

public class ShiftHelper {

    // Only a static helper class, no need to construct
    private ShiftHelper() {}

    public static float calculateShiftHours(Shift shift) {
        Period difference = new Period(shift.getStartTime(), shift.getEndTime());
        int v = difference.getMinutes() + (difference.getHours() * 60);
        return v / 60f;
    }

    public static float calculateShiftHours(LocalTime startTime, LocalTime endTime) {
        Period difference;
        if (startTime.isBefore(endTime)) {
            difference = new Period(startTime, endTime);
        } else {
            return calculateShiftHours(DateTime.now().withTime(startTime),
                    DateTime.now().plusDays(1).withTime(endTime));
        }

        return (difference.getMinutes() + (difference.getHours() * 60)) / 60f;
    }

    public static float calculateShiftHours(DateTime startTime, DateTime endTime) {
        Duration difference;
        if (startTime.equals(endTime)) {
            return 0;
        }

        difference = new Duration(startTime, endTime);
        return difference.getStandardMinutes() / 60f;
    }

    public static boolean shiftIsOnWeekend(Shift shift) {
        int startOfShiftDay = shift.getStartTime().getDayOfWeek();
        int endOfShiftDay = shift.getEndTime().getDayOfWeek();

        return startOfShiftDay == DayOfWeek.SATURDAY.getValue() ||
                startOfShiftDay == DayOfWeek.SUNDAY.getValue() ||
                endOfShiftDay == DayOfWeek.SATURDAY.getValue() ||
                endOfShiftDay == DayOfWeek.SUNDAY.getValue();
    }
}
