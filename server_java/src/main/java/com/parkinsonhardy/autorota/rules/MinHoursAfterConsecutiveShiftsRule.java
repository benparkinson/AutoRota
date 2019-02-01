package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.helpers.IntegerMatcher;
import com.parkinsonhardy.autorota.helpers.ShiftHelper;
import org.joda.time.Duration;

import java.util.List;

public class MinHoursAfterConsecutiveShiftsRule implements Rule {

    private final String shiftType;
    private final IntegerMatcher consecutiveShiftCountMatcher;
    private final int hoursRestAfterConsecutiveShifts;

    public MinHoursAfterConsecutiveShiftsRule(String shiftType, int consecutiveShiftCount,
                                              int hoursRestAfterConsecutiveShifts) {
        this.shiftType = shiftType;
        this.consecutiveShiftCountMatcher = new IntegerMatcher(consecutiveShiftCount);
        this.hoursRestAfterConsecutiveShifts = hoursRestAfterConsecutiveShifts;
    }

    public MinHoursAfterConsecutiveShiftsRule(String shiftType, IntegerMatcher consecutiveShiftCount,
                                              int hoursRestAfterConsecutiveShifts) {
        this.shiftType = shiftType;
        this.consecutiveShiftCountMatcher = consecutiveShiftCount;
        this.hoursRestAfterConsecutiveShifts = hoursRestAfterConsecutiveShifts;
    }

    @Override
    public String getName() {
        return String.format("Min Hours After Consecutive Shifts Rule: %s, hours rest required: %d,consecutive shifts to check: %s",
                shiftType, hoursRestAfterConsecutiveShifts, consecutiveShiftCountMatcher.toString());
    }

    @Override
    public boolean shiftsPassesRule(List<Shift> shifts) {
        if (shifts.size() < 2) {
            return true;
        }

        int consecutiveShiftCount = 0;
        Shift lastShiftChecked = null;
        for (Shift shift : shifts) {
            boolean shiftToCount = shift.getShiftType().equals(shiftType);
            // if next shift type is different, check hours after shift
            if (consecutiveShiftsBreakRule(consecutiveShiftCount, lastShiftChecked, shift, shiftToCount)) {
                return false;
            }

            if (shiftToCount)
                consecutiveShiftCount++;
            else
                consecutiveShiftCount = 0;

            if (lastShiftChecked != null && moreThanOneDayBetweenShifts(lastShiftChecked, shift)
                    && consecutiveShiftCountMatcher.matches(consecutiveShiftCount)) {
                // if there's a day gap between shifts, check the rule for failure
                if (!consecutiveShiftCountMatcher.matches(consecutiveShiftCount + 1)
                        && notEnoughHoursRestBetweenShifts(lastShiftChecked, shift)) {
                    return false;
                }

                consecutiveShiftCount = 0;
            }

            lastShiftChecked = shift;
        }
        return true;
    }

    private boolean consecutiveShiftsBreakRule(int consecutiveShiftCount, Shift lastShiftChecked, Shift shift, boolean shiftToCount) {
        return lastShiftChecked != null && consecutiveShiftCountMatcher.matches(consecutiveShiftCount) &&
                !(shiftToCount && consecutiveShiftCountMatcher.matches(consecutiveShiftCount + 1))
                && notEnoughHoursRestBetweenShifts(lastShiftChecked, shift);
    }

    private boolean notEnoughHoursRestBetweenShifts(Shift lastShiftChecked, Shift shift) {
        return ShiftHelper.calculateShiftHours(lastShiftChecked.getEndTime(), shift.getStartTime()) <
                hoursRestAfterConsecutiveShifts;
    }

    private boolean moreThanOneDayBetweenShifts(Shift lastShiftChecked, Shift shift) {
        Duration duration = new Duration(lastShiftChecked.getStartTime(), shift.getStartTime());
        return duration.getStandardDays() > 1;
    }

}
