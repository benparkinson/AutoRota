package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.engine.ShiftHelper;
import org.joda.time.LocalTime;

import java.util.List;

public class MaxHoursPerWeekRule implements Rule {

    private int maxHours;

    public MaxHoursPerWeekRule(int maxHours) {
        this.maxHours = maxHours;
    }

    @Override
    public boolean employeeCanWorkShift(Employee employee, Shift shift) {
        List<Shift> shifts = employee.getShifts();

        if (shift.getStartTime().getWeekOfWeekyear() != shift.getEndTime().getWeekOfWeekyear()) {
            int newShiftWeek = shift.getStartTime().getWeekOfWeekyear();
            int hoursPerShiftWeek = ShiftHelper.CalculateShiftHours(shift.getStartTime().toLocalTime(),
                    new LocalTime(0, 0, 0));
            boolean firstWeek = checkWeekCanTakeMoreHours(newShiftWeek, hoursPerShiftWeek, shifts);
            newShiftWeek = shift.getEndTime().getWeekOfWeekyear();
            hoursPerShiftWeek = ShiftHelper.CalculateShiftHours(new LocalTime(0, 0, 0), shift.getEndTime().toLocalTime());
            boolean secondWeek = checkWeekCanTakeMoreHours(newShiftWeek, hoursPerShiftWeek, shifts);
            return firstWeek && secondWeek;
        } else {
            if (shifts.size() == 0)
                return ShiftHelper.CalculateShiftHours(shift) <= maxHours;

            int newShiftWeek = shift.getStartTime().getWeekOfWeekyear();
            int hoursPerShiftWeek = ShiftHelper.CalculateShiftHours(shift);
            return checkWeekCanTakeMoreHours(newShiftWeek, hoursPerShiftWeek, shifts);
        }
    }

    private boolean checkWeekCanTakeMoreHours(int weekOfYear, int hoursToAdd, List<Shift> shifts) {
        int hoursPerShiftWeek = hoursToAdd;
        for (Shift shiftToCheck : shifts) {
            if (shiftToCheck.getStartTime().getWeekOfWeekyear() == weekOfYear) {
                LocalTime endTime = shiftToCheck.getEndTime().toLocalTime();
                if (shiftToCheck.getStartTime().getWeekOfWeekyear() !=
                        shiftToCheck.getEndTime().getWeekOfWeekyear()) {
                    endTime = new LocalTime(0, 0, 0);
                }

                hoursPerShiftWeek += ShiftHelper.CalculateShiftHours(shiftToCheck.getStartTime().toLocalTime(), endTime);
                if (hoursPerShiftWeek > maxHours) {
                    return false;
                }
            } else if (shiftToCheck.getEndTime().getWeekOfWeekyear() == weekOfYear) {
                hoursPerShiftWeek += ShiftHelper.CalculateShiftHours(new LocalTime(0,0,0), shiftToCheck.getEndTime().toLocalTime());
                if (hoursPerShiftWeek > maxHours) {
                    return false;
                }
            }
        }
        return true;
    }
}
