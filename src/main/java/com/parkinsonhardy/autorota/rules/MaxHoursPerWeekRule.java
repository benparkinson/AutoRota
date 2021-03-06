package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.helpers.ShiftHelper;
import org.joda.time.DateTime;

import java.util.List;

public class MaxHoursPerWeekRule implements Rule {

    private int maxHours;

    public MaxHoursPerWeekRule(int maxHours) {
        this.maxHours = maxHours;
    }

    @Override
    public String getName() {
        return String.format("Max Hours Per Week Rule: %d", maxHours);
    }

    @Override
    public boolean shiftsPassesRule(List<Shift> shifts) {
        for (Shift shift : shifts) {
            float totalHours = 0;
            DateTime sevenDaysAgo = shift.getEndTime().minusDays(7);

            // go back previous 7 days and sum hours
            for (int i = shifts.size() - 1; i > -1; i--) {
                Shift toAdd = shifts.get(i);
                if (toAdd.getStartTime().isAfter(shift.getStartTime()))
                    continue;

                if (toAdd.getEndTime().isBefore(sevenDaysAgo)) {
                    break;
                }
                DateTime startTime = toAdd.getStartTime();
                if (toAdd.getStartTime().isBefore(sevenDaysAgo)) {
                    // part of the shift is within the 7 day window, calculate from the overlap
                    startTime = sevenDaysAgo;
                }

                totalHours += ShiftHelper.calculateShiftHours(startTime, toAdd.getEndTime());
            }

            if (totalHours > maxHours)
                return false;
        }
        return true;
    }
}
