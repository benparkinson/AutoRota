package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.engine.ShiftHelper;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MaxConsecutiveWeekendsRule implements Rule {

    private final int maxConsecutiveWeekends;

    public MaxConsecutiveWeekendsRule(int maxConsecutiveWeekends) {
        this.maxConsecutiveWeekends = maxConsecutiveWeekends;
    }

    @Override
    public boolean employeeCanWorkShift(Employee employee, Shift shift) {
        if (!shiftIsOnWeekend(shift)) {
            return true;
        }

        List<Shift> shifts = employee.getShifts();

        if (shifts.size() == 0)
            return true;

        // sort for sanity, should already be sorted
        Collections.sort(shifts);

        int consecutiveWeekends = 1;
        Set<Integer> weeksOfYearWithShifts = new HashSet<>();
        weeksOfYearWithShifts.add(shift.getStartTime().getWeekOfWeekyear());

        boolean foundPreviousShift = false;
        Shift lastWeekendShift = null;
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

            if (!shiftIsOnWeekend(shiftToCheck)) {
                continue;
            }

            if (lastWeekendShift == null) {
                lastWeekendShift = shift;
            }

            int lastWeekendShiftWeek = lastWeekendShift.getStartTime().getWeekOfWeekyear();
            int thisWeekendShiftWeek = shiftToCheck.getStartTime().getWeekOfWeekyear();

            if (Math.abs(lastWeekendShiftWeek - thisWeekendShiftWeek) > 1) {
                return true;
            }


            if (weeksOfYearWithShifts.add(shiftToCheck.getStartTime().getWeekOfWeekyear())) {
                consecutiveWeekends++;
            }

            if (consecutiveWeekends > maxConsecutiveWeekends) {
                return false;
            }

            lastWeekendShift = shiftToCheck;
        }

        return true;
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
