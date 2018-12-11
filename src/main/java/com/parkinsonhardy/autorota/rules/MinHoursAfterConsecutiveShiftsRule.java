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
        return "Min Hours After Consecutive Shifts Rule: " + shiftType + ", hours rest required: " + hoursRestAfterConsecutiveShifts + "," +
                "consecutive shifts to check: " + consecutiveShiftCountMatcher.toString();
    }

    @Override
    public boolean shiftsPassesRule(List<Shift> shifts) {
        if (shifts.size() < 2) {
            return true;
        }

        int consecutiveShiftCount = 0;
        boolean checkNextShift = false;
        Shift lastShiftChecked = null;
        for (Shift shift : shifts) {
            if (checkNextShift) {
                if (!(shift.getShiftType().equals(shiftType) && consecutiveShiftCountMatcher.matches(consecutiveShiftCount + 1))) {
                    if (ShiftHelper.CalculateShiftHours(lastShiftChecked.getEndTime(), shift.getStartTime()) <
                            hoursRestAfterConsecutiveShifts)
                        return false;

                    checkNextShift = false;
                }
            }

            if (shift.getShiftType().equals(shiftType))
                consecutiveShiftCount++;
            else
                consecutiveShiftCount = 0;

            if (lastShiftChecked != null) {
                if (moreThanOneDayBetweenShifts(lastShiftChecked, shift))
                    consecutiveShiftCount = 0;
            }

            if (consecutiveShiftCountMatcher.matches(consecutiveShiftCount)) {
                checkNextShift = true;
            }

            lastShiftChecked = shift;
        }
        return true;
    }

    private boolean moreThanOneDayBetweenShifts(Shift lastShiftChecked, Shift shift) {
        Duration duration = new Duration(lastShiftChecked.getStartTime(), shift.getStartTime());
        return duration.getStandardDays() > 1;
    }

}
