package com.parkinsonhardy.autorota.util;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.helpers.ShiftHelper;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.List;

public class RotaUtils {

    private RotaUtils() {}

    public static String stringifyRota(List<Employee> employees, DateTime startDate, DateTime endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append(",");
        String prefix = "";
        for (Employee employee : employees) {
            sb.append(prefix);
            prefix = ",";
            sb.append(employee.getName());
        }

        sb.append("\n");

        for (DateTime dt = startDate; dt.isBefore(endDate); dt = dt.plusDays(1)) {
            sb.append(dt.toString("yyyy-MM-dd EEE")).append(",");

            prefix = "";
            for (Employee employee : employees) {
                sb.append(prefix);
                prefix = ",";
                for (Shift shift : employee.getShifts()) {
                    if (shift.getStartTime().withTimeAtStartOfDay().equals(dt)) {
                        sb.append(shift.getShiftType());
                        break;
                    }
                }
            }
            sb.append("\n");
        }

        sb.append("Number of Days,");
        prefix = "";
        for (Employee employee : employees) {
            int numberOfDays = countShifts(employee, "Day");
            sb.append(prefix);
            prefix = ",";
            sb.append(numberOfDays);
        }
        sb.append("\n");

        sb.append("Number of LongDays,");
        prefix = "";
        for (Employee employee : employees) {
            int numberOfDays = countShifts(employee, "LongDay");
            sb.append(prefix);
            prefix = ",";
            sb.append(numberOfDays);
        }
        sb.append("\n");

        sb.append("Number of Nights,");
        prefix = "";
        for (Employee employee : employees) {
            int numberOfDays = countShifts(employee, "Night");
            sb.append(prefix);
            prefix = ",";
            sb.append(numberOfDays);
        }
        sb.append("\n");

        sb.append("Total Hours,");
        prefix = "";
        for (Employee employee : employees) {
            int totalHours = countHours(employee);
            sb.append(prefix);
            prefix = ",";
            sb.append(totalHours);
        }
        sb.append("\n");

        sb.append("Average hours per week,");
        prefix = "";
        for (Employee employee : employees) {
            int totalHours = countHours(employee);
            float averageHours = (float) totalHours / (new Duration(startDate, endDate).getStandardDays() / 7f);
            sb.append(prefix);
            prefix = ",";
            sb.append(averageHours);
        }
        sb.append("\n\n");
        return sb.toString();
    }

    private static int countHours(Employee employee) {
        int totalHours = 0;
        for (Shift shift : employee.getShifts()) {
            totalHours += ShiftHelper.calculateShiftHours(shift.getStartTime(), shift.getEndTime());
        }
        return totalHours;
    }

    private static int countShifts(Employee employee, String shiftType) {
        int totalShifts = 0;
        for (Shift shift : employee.getShifts()) {
            if (shift.getShiftType().equals(shiftType)) {
                totalShifts += 1;
            }
        }
        return totalShifts;
    }
}
