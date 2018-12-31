package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.helpers.ShiftHelper;
import org.joda.time.DateTime;

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
    public boolean shiftsPassesRule(List<Shift> shifts) {
        if (shifts.size() < 2) {
            return true;
        }

        Shift lastShift = null;
        for (Shift shift : shifts) {
            if (lastShift == null) {
                lastShift = shift;
                continue;
            }

            DateTime endTime = lastShift.getEndTime();
            DateTime startTime = shift.getStartTime();

            if (ShiftHelper.CalculateShiftHours(endTime, startTime) < minHours)
                return false;

            lastShift = shift;
        }
        return true;
    }
}
