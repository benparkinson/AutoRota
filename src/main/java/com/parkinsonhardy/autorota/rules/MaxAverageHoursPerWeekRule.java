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
                    endTime = LocalTime.MIDNIGHT;
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
            int employeeTotalHours = 0;
            for (Shift shift : employee.getShifts()) {
                employeeTotalHours += ShiftHelper.CalculateShiftHours(shift);
            }
            employee.setPriorityWeight(employeeTotalHours);
        }
    }

    @Override
    public void finalCheck(List<Employee> employees) throws RotaException {
        // check the results
    }
}
