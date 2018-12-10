package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class MaxConsecutiveShiftRule implements Rule {

    private static Logger logger = Logger.getLogger(MaxConsecutiveShiftRule.class.getName());

    private String shiftType;
    private int maxConsecutiveShifts;

    public MaxConsecutiveShiftRule(String shiftType, int maxConsecutiveShifts) {
        this.shiftType = shiftType;
        this.maxConsecutiveShifts = maxConsecutiveShifts;
    }

    @Override
    public String getName() {
        return String.format("Max Consecutive Shift Rule: %s, max shifts: %d", shiftType, maxConsecutiveShifts);
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
        Shift lastShift = null;
        for (int i = shifts.size() - 1; i > -1; i--) {
            Shift shiftToCheck = shifts.get(i);
            if (!foundPreviousShift) {
                if (shiftToCheck.getEndTime().isBefore(shift.getStartTime())
                        || shiftToCheck.getEndTime().equals(shift.getStartTime())) {
                    foundPreviousShift = true;
                    int dayOfYear = shiftToCheck.getStartTime().getDayOfYear();
                    int dayOfYear1 = shift.getStartTime().getDayOfYear();
                    if (dayOfYear1 - dayOfYear > 1) {
                        return true;
                    }
                } else {
                    continue;
                }
            }

            if (shiftToCheck.getShiftType().equals(shift.getShiftType())) {
                if (lastShift != null) {
                    int dayOfYear = lastShift.getStartTime().getDayOfYear();
                    int dayOfYear1 = shiftToCheck.getStartTime().getDayOfYear();
                    if (dayOfYear - dayOfYear1 > 1) {
                        return true;
                    }
                }
                numberOfSameShift++;
                if (numberOfSameShift >= maxConsecutiveShifts) {
                    logger.info(String.format("Cannot assign shift to: %s as it breached max consecutive shifts of %d for %s",
                            employee.getName(), maxConsecutiveShifts, shiftType));
                    return false;
                }
            } else {
                return true;
            }
            lastShift = shiftToCheck;
        }
        return true;
    }
}
