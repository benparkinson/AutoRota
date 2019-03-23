package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Shift;
import org.joda.time.Duration;

import java.util.List;

public class MaxConsecutiveShiftRule implements Rule {

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
    public boolean shiftsPassesRule(List<Shift> shifts) {
        shifts.sort(Shift::compareTo);

        if (shifts.size() < 2) {
            return true;
        }

        int consecutiveShiftCount = 0;
        Shift lastShiftChecked = null;
        for (Shift shift : shifts) {
            if (lastShiftChecked != null && moreThanOneDayBetweenShifts(lastShiftChecked, shift))
                consecutiveShiftCount = 0;


            if (shift.getShiftType().equals(shiftType))
                consecutiveShiftCount++;
            else
                consecutiveShiftCount = 0;


            if (consecutiveShiftCount > maxConsecutiveShifts)
                return false;

            lastShiftChecked = shift;
        }
        return true;
    }

    private boolean moreThanOneDayBetweenShifts(Shift lastShiftChecked, Shift shift) {
        Duration duration = new Duration(lastShiftChecked.getStartTime(), shift.getStartTime());
        return duration.getStandardDays() > 1;
    }
}
