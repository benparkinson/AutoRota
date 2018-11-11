package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import org.joda.time.Period;

import java.util.Collections;
import java.util.List;

public class MinHoursBetweenShiftsRule implements Rule {

    private int minHours;

    public MinHoursBetweenShiftsRule(int minHours) {
        this.minHours = minHours;
    }

    @Override
    public boolean employeeCanWorkShift(Employee employee, Shift shift) {
        List<Shift> shifts = employee.getShifts();

        if (shifts.size() == 0)
            return true;

        // sort for sanity, should already be sorted
        Collections.sort(shifts);

        boolean foundPreviousShift = false;
        for (int i = shifts.size() -1; i > -1; i--) {
            Shift shiftToCheck = shifts.get(i);
            if (!foundPreviousShift) {
                if (shiftToCheck.getEndTime().isBefore(shift.getStartTime()) ||
                        shiftToCheck.getEndTime().equals(shift.getStartTime())) {
                    foundPreviousShift = true;
                } else {
                    continue;
                }
            }

            Period difference = new Period(shiftToCheck.getEndTime(), shift.getStartTime());
            if (difference.getHours() < minHours)
                return false;
        }
        return true;
    }
}
