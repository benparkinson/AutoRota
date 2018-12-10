package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.engine.ShiftHelper;

import java.util.Collections;
import java.util.List;

public class MinHoursBetweenShiftsRule implements Rule {

    private int minHours;

    public MinHoursBetweenShiftsRule(int minHours) {
        this.minHours = minHours;
    }

    @Override
    public String getName() {
        return String.format("Min Hours Between Shifts: %d", minHours);
    }

    @Override
    public boolean employeeCanWorkShift(Employee employee, Shift shift) {
        List<Shift> shifts = employee.getShifts();

        if (shifts.size() == 0)
            return true;

        // sort for sanity, should already be sorted
        Collections.sort(shifts);

        boolean foundPreviousShift = false;
        for (int i = shifts.size() - 1; i > -1; i--) {
            Shift shiftToCheck = shifts.get(i);
            if (!foundPreviousShift) {
                if (shiftToCheck.getEndTime().isBefore(shift.getStartTime()) ||
                        shiftToCheck.getEndTime().equals(shift.getStartTime())) {
                    foundPreviousShift = true;
                } else {
                    continue;
                }
            }

            int shiftHoursDifference = ShiftHelper.CalculateShiftHours(shiftToCheck.getEndTime(), shift.getStartTime());
            if (shiftHoursDifference < minHours)
                return false;
        }

        boolean foundNextShift = false;

        for (int i = 0; i < shifts.size(); i++) {
            Shift shiftToCheck = shifts.get(i);
            if (!foundNextShift) {
                if (shiftToCheck.getStartTime().isAfter(shift.getEndTime()) ||
                        shiftToCheck.getStartTime().equals(shift.getEndTime())) {
                    foundNextShift = true;
                } else {
                    continue;
                }
            }

            int shiftHoursDifference = ShiftHelper.CalculateShiftHours(shift.getEndTime(), shiftToCheck.getStartTime());
            if (shiftHoursDifference < minHours)
                return false;
        }
        return true;
    }
}
