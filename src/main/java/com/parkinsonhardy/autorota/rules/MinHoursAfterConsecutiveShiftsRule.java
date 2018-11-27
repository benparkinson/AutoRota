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
    public boolean employeeCanWorkShift(Employee employee, Shift shift) {
        List<Shift> shifts = employee.getShifts();

        if (shifts.size() == 0)
            return true;

        // sort for sanity, should already be sorted
        Collections.sort(shifts);

        Shift previousShift = null;
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

            if (shiftToCheck.getShiftType().equals(shiftType)) {
                numberOfSameShift++;
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
        }

        return true;
    }
}
