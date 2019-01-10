package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.helpers.ShiftHelper;
import org.joda.time.LocalTime;

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
        int hoursPerWeek = 0;
        int currentWeek = -1;
        int hoursToCarry = 0;

        for (Shift shift : shifts) {
            int weekOfWeekyear = shift.getStartTime().getWeekOfWeekyear();
            if (weekOfWeekyear != currentWeek) {
                hoursPerWeek = hoursToCarry;
                currentWeek = weekOfWeekyear;
            }

            if (shift.getStartTime().getWeekOfWeekyear() != shift.getEndTime().getWeekOfWeekyear()) {
                int hoursPerShiftWeek = ShiftHelper.CalculateShiftHours(shift.getStartTime().toLocalTime(),
                        LocalTime.MIDNIGHT);
                hoursPerWeek += hoursPerShiftWeek;
                hoursToCarry = ShiftHelper.CalculateShiftHours(LocalTime.MIDNIGHT, shift.getEndTime().toLocalTime());
            } else {
                hoursToCarry = 0;
                int hoursPerShift = ShiftHelper.CalculateShiftHours(shift);
                hoursPerWeek += hoursPerShift;
            }

            if (hoursPerWeek > maxHours)
                return false;
        }
        return true;
    }
}
