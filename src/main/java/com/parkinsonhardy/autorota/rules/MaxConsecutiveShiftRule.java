package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;

import java.util.Collections;
import java.util.List;

public class MaxConsecutiveShiftRule implements Rule {

    private String shiftType;
    private int maxConsecutiveShifts;

    public MaxConsecutiveShiftRule(String shiftType, int maxConsecutiveShifts) {
        this.shiftType = shiftType;
        this.maxConsecutiveShifts = maxConsecutiveShifts;
    }

    @Override
    public boolean employeeCanWorkShift(Employee employee, Shift shift) {
        List<Shift> shifts = employee.getShifts();

        if (shifts.size() == 0)
            return true;

        if (!shift.getShiftType().equals(shiftType))
            return true;

        // sort for sanity, should already be sorted
        Collections.sort(shifts);

        boolean foundPreviousShift = false;
        int numberOfSameShift = 0;
        for (int i = shifts.size() - 1; i > -1; i--) {
            Shift shiftToCheck = shifts.get(i);
            if (!foundPreviousShift) {
                if (shiftToCheck.getEndTime().isBefore(shift.getStartTime())
                        || shiftToCheck.getEndTime().equals(shift.getStartTime())) {
                    foundPreviousShift = true;
                } else {
                    continue;
                }
            }

            if (shiftToCheck.getShiftType().equals(shift.getShiftType())) {
                numberOfSameShift++;
                if (numberOfSameShift >= maxConsecutiveShifts)
                    return false;
            } else {
                return true;
            }
        }
        return true;
    }
}
