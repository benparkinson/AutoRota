package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Shift;
import org.joda.time.Duration;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoMoreThanOneConsecutiveWeekendsRule implements Rule {

    private static final int ONE = 1;

    public NoMoreThanOneConsecutiveWeekendsRule() { }

    @Override
    public boolean shiftsPassesRule(List<Shift> shifts) {
        if (shifts.size() < 2) {
            return true;
        }

        Set<Integer> weeksOfYearWithShifts = new HashSet<>();
        int consecutiveWeekendShiftCount = 0;
        Shift lastWeekendShiftChecked = null;
        for (Shift shift : shifts) {
            if (!shiftIsOnWeekend(shift))
                continue;

            if (weeksOfYearWithShifts.add(shift.getStartTime().getWeekOfWeekyear()))
                consecutiveWeekendShiftCount++;

            if (lastWeekendShiftChecked != null) {
                if (moreThanOneWeekBetweenShifts(lastWeekendShiftChecked, shift))
                    consecutiveWeekendShiftCount = 0;
            }

            if (consecutiveWeekendShiftCount > ONE)
                return false;

            lastWeekendShiftChecked = shift;
        }
        return true;
    }

    private boolean moreThanOneWeekBetweenShifts(Shift lastShiftChecked, Shift shift) {
        Duration duration = new Duration(lastShiftChecked.getStartTime(), shift.getStartTime());
        return duration.getStandardDays() > 7;
    }

    private boolean shiftIsOnWeekend(Shift shift) {
        int startOfShiftDay = shift.getStartTime().getDayOfWeek();
        int endOfShiftDay = shift.getEndTime().getDayOfWeek();

        if (startOfShiftDay == DayOfWeek.SATURDAY.getValue() ||
                startOfShiftDay == DayOfWeek.SUNDAY.getValue() ||
                endOfShiftDay == DayOfWeek.SATURDAY.getValue() ||
                endOfShiftDay == DayOfWeek.SUNDAY.getValue()) {
            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "Max consecutive weekends rule";
    }
}
