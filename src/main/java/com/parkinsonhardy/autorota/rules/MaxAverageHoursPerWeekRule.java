package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.*;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.List;
import java.util.Map;

public class MaxAverageHoursPerWeekRule implements HolisticRule {

    private int maxAverageHours;

    public MaxAverageHoursPerWeekRule(int maxAverageHours) {
        this.maxAverageHours = maxAverageHours;
    }

    public boolean employeeCanWorkShift(Employee employee, Shift shift) {
        List<Shift> shifts = employee.getShifts();

        if (shifts.size() == 0)
            return ShiftHelper.CalculateShiftHours(shift) <= maxAverageHours;

        int weekCount;
        int newShiftWeek = shift.getStartTime().getWeekOfWeekyear();
        int hoursPerShiftWeek = ShiftHelper.CalculateShiftHours(shift);
        for (Shift shiftToCheck : shifts) {
            if (shiftToCheck.getStartTime().getWeekOfWeekyear() == newShiftWeek) {
                hoursPerShiftWeek += ShiftHelper.CalculateShiftHours(shift);
                if (hoursPerShiftWeek > maxAverageHours) {
                    return false;
                }
            }
        }
        return true;
    }

    // other tests:
        // check multiple weeks
        // check fails if last hour of shift breaks
        // check fails if breaks with half of shift on second week
        // check passes if lots of hours per week but none over threshold

    @Override
    public boolean passesPreCheck(DateTime startDate, DateTime endDate, Map<String, ShiftDefinition> shiftDefinitions,
                                  List<ShiftRequirement> shiftRequirements, List<Employee> employees) {
        // calculate required hours per week and whether or not it'll be possible to assign shifts and not break rule
        int totalPerWeek = 0;
        int week = startDate.getWeekOfWeekyear();
        int hoursCarried = 0;
        for (DateTime dt = startDate.withTimeAtStartOfDay();
             dt.isBefore(endDate.withTimeAtStartOfDay().getMillis());
             dt = dt.plusDays(1)) {
            if (dt.getWeekOfWeekyear() != week) {
                week = dt.getWeekOfWeekyear();
                if ((totalPerWeek / employees.size()) > maxAverageHours) {
                    return false;
                }
                totalPerWeek = hoursCarried;
                hoursCarried = 0;
            }
            for (ShiftRequirement shiftRequirement : shiftRequirements) {
                if (shiftRequirement.getDayOfWeek() != dt.getDayOfWeek()) {
                    continue;
                }
                ShiftDefinition shiftDefinition = shiftDefinitions.get(shiftRequirement.getShiftType());
                LocalTime endTime = shiftDefinition.getEndTime();
                if (shiftDefinition.getStartTime().isAfter(shiftDefinition.getEndTime())
                        && dt.getDayOfWeek() == 7) {
                    endTime = new LocalTime(0, 0, 0);
                    hoursCarried = ShiftHelper.CalculateShiftHours(endTime, shiftDefinition.getEndTime());
                }

                int hoursPerShift = ShiftHelper.CalculateShiftHours(shiftDefinition.getStartTime(), endTime);
                totalPerWeek += hoursPerShift * shiftRequirement.getMinEmployees();
            }
        }
        return totalPerWeek / employees.size() <= maxAverageHours;
    }

    @Override
    public void interrimCheck(List<Employee> employees) {
        for (Employee employee : employees) {
            int runningTotal = 0;
            for (Shift shift : employee.getShifts()) {
                int weekOfWeekyear = shift.getStartTime().getWeekOfWeekyear();
                int shiftHours = ShiftHelper.CalculateShiftHours(shift);
            }
        }
        // check average hours per week for each employee and reweight based on that
    }

    @Override
    public void finalCheck(List<Employee> employees) throws RotaException {
        // check the results
    }
}
