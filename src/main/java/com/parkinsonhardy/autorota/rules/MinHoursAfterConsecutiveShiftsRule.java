package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.engine.ShiftHelper;

import java.util.Collections;
import java.util.List;

public class MinHoursAfterConsecutiveShiftsRule implements Rule {

    private final String shiftType;
    private final IntegerMatcher consecutiveShiftCount;
    private final int hoursRestAfterConsecutiveShifts;

    public MinHoursAfterConsecutiveShiftsRule(String shiftType, int consecutiveShiftCount,
                                              int hoursRestAfterConsecutiveShifts) {
        this.shiftType = shiftType;
        this.consecutiveShiftCount = new IntegerMatcher(consecutiveShiftCount);
        this.hoursRestAfterConsecutiveShifts = hoursRestAfterConsecutiveShifts;
    }

    public MinHoursAfterConsecutiveShiftsRule(String shiftType, IntegerMatcher consecutiveShiftCount,
                                              int hoursRestAfterConsecutiveShifts) {
        this.shiftType = shiftType;
        this.consecutiveShiftCount = consecutiveShiftCount;
        this.hoursRestAfterConsecutiveShifts = hoursRestAfterConsecutiveShifts;
    }

    @Override
    public String getName() {
        return "Min Hours After Consecutive Shifts Rule: " + shiftType + ", hours rest required: " + hoursRestAfterConsecutiveShifts + "," +
                "consecutive shifts to check: " + consecutiveShiftCount.toString();
    }

    @Override
    public boolean employeeCanWorkShift(Employee employee, Shift shift) {
        List<Shift> shifts = employee.getShifts();

        if (shifts.size() == 0)
            return true;

        // sort for sanity, should already be sorted
        Collections.sort(shifts);

        Shift previousShift = null;
        Shift lastShiftChecked = null;
        int numberOfSameShift = 0;
        for (int i = shifts.size() - 1; i > -1; i--) {
            Shift shiftToCheck = shifts.get(i);
            if (previousShift == null) {
                if (shiftToCheck.getEndTime().isBefore(shift.getStartTime())
                        || shiftToCheck.getEndTime().equals(shift.getStartTime())) {
                    previousShift = shiftToCheck;
                } else {
                    continue;
                }
            }

            if (lastShiftChecked != null) {
                // check if day has been skipped between shifts (then don't apply rule)
                int dayOfYear = shiftToCheck.getStartTime().getDayOfYear();
                int dayOfYear1 = lastShiftChecked.getStartTime().getDayOfYear();

                if (!(shiftToCheck.getShiftType().equals(shiftType)
                        && consecutiveShiftCount.matches(numberOfSameShift + 1))) {
                    if (dayOfYear1 - dayOfYear > 1) {
                        return true;
                    }
                }
            }

            if (shiftToCheck.getShiftType().equals(shiftType)) {
                numberOfSameShift++;
            } else {
                // not consecutive shifts
                return true;
            }

            if (consecutiveShiftCount.matches(numberOfSameShift)) {
                // check to see if the rule applies to the next number of consecutive shifts too, if so then check it next time
                if (consecutiveShiftCount.matches(numberOfSameShift + 1) && shift.getShiftType().equals(shiftType)) {
                    continue;
                }
                int hoursBetweenShifts = ShiftHelper.CalculateShiftHours(previousShift.getEndTime(),
                        shift.getStartTime());
                if (hoursBetweenShifts < hoursRestAfterConsecutiveShifts) {
                    return false;
                }
            }
            lastShiftChecked = shiftToCheck;
        }

        return true;
    }
}
