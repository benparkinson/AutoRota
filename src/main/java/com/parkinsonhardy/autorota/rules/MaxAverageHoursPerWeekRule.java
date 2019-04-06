package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import com.parkinsonhardy.autorota.engine.ShiftRequirement;
import com.parkinsonhardy.autorota.helpers.ShiftHelper;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MaxAverageHoursPerWeekRule implements HolisticRule {

    private int maxAverageHours;

    public MaxAverageHoursPerWeekRule(int maxAverageHours) {
        this.maxAverageHours = maxAverageHours;
    }

    @Override
    public boolean passesPreCheck(DateTime startDate, DateTime endDate, Map<String, ShiftDefinition> shiftDefinitions,
                                  List<ShiftRequirement> shiftRequirements, List<Employee> employees) {
        // calculate required hours per week and whether or not it'll be possible to assign shifts and not break rule
        float totalHours = 0;
        int week = startDate.getWeekOfWeekyear();
        int weekCount = 1;
        for (DateTime dt = startDate.withTimeAtStartOfDay();
             dt.isBefore(endDate.withTimeAtStartOfDay().getMillis());
             dt = dt.plusDays(1)) {
            if (dt.getWeekOfWeekyear() != week) {
                week = dt.getWeekOfWeekyear();
                weekCount++;
            }
            for (ShiftRequirement shiftRequirement : shiftRequirements) {
                if (!shiftRequirement.shiftRequiredOnDay(DayOfWeek.of(dt.getDayOfWeek()))) {
                    continue;
                }
                ShiftDefinition shiftDefinition = shiftDefinitions.get(shiftRequirement.getShiftType());
                LocalTime endTime = shiftDefinition.getEndTime();
                float hoursPerShift = ShiftHelper.calculateShiftHours(shiftDefinition.getStartTime(), endTime);
                totalHours += hoursPerShift * shiftRequirement.getMinEmployees();
            }
        }
        return ((double)totalHours / (double)employees.size() / (double)weekCount) <= maxAverageHours;
    }

    @Override
    public boolean passesFinalCheck(List<Employee> employees) {
        for (Employee employee : employees) {
            if (employee.getShifts().isEmpty()) {
                continue;
            }
            Collections.sort(employee.getShifts());
            float totalHours = 0;
            Integer week = null;
            int weekCount = 0;
            for (Shift shift : employee.getShifts()) {
                if (week == null || shift.getStartTime().getWeekOfWeekyear() != week) {
                    week = shift.getStartTime().getWeekOfWeekyear();
                    weekCount++;
                }
                if (shift.getEndTime().getWeekOfWeekyear() != week) {
                    week = shift.getEndTime().getWeekOfWeekyear();
                    weekCount++;
                }
                totalHours += ShiftHelper.calculateShiftHours(shift);
            }

            if (weekCount == 0 || (double)totalHours / (double)weekCount > maxAverageHours) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "MaxAverageHoursPerWeekRule{" +
                "max=" + maxAverageHours +
                '}';
    }
}
