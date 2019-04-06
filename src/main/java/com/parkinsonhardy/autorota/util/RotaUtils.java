package com.parkinsonhardy.autorota.util;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.helpers.ShiftHelper;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.parkinsonhardy.autorota.helpers.ShiftHelper.shiftIsOnWeekend;

public class RotaUtils {

    private RotaUtils() {
    }

    // this is now getting to too many loops through the same list
    // ideally we would return actual objects that can be manipulated by the GUI as required rather than
    // returning a big fat string, so that's the preferred solution rather than a more performant way of making
    // a big fat string.
    public static String stringifyRota(List<Employee> employees, DateTime startDate, DateTime endDate) {
        Set<String> shiftTypes = new HashSet<>();
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
                    shiftTypes.add(shift.getShiftType());
                    if (shift.getStartTime().withTimeAtStartOfDay().equals(dt)) {
                        sb.append(shift.getShiftType());
                        break;
                    }
                }
            }
            sb.append("\n");
        }

        for (String shiftType : shiftTypes) {
            sb.append(String.format("Number of %s shifts,", shiftType));
            prefix = "";
            for (Employee employee : employees) {
                int shiftCount = countShifts(employee, shiftType);
                sb.append(prefix);
                prefix = ",";
                sb.append(shiftCount);
            }
            sb.append("\n");
        }

        sb.append("Max # of Consecutive Shifts,");
        prefix = "";
        for (Employee employee : employees) {
            sb.append(prefix);
            prefix = ",";
            int maxConsecutive = countConsecutiveShifts(employee);
            sb.append(maxConsecutive);
        }
        sb.append("\n");

        sb.append("# Weekends Worked,");
        prefix = "";
        for (Employee employee : employees) {
            sb.append(prefix);
            prefix = ",";
            int weekendsWorked = countWeekendsWorked(employee);
            sb.append(weekendsWorked);
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
        return sb.toString();
    }

    // assumes shifts are sorted...they should be at this point
    private static int countConsecutiveShifts(Employee employee) {
        int maxConsecutive = 0;
        int currentConsecutive = 0;
        DateTime previousShiftStartTime = null;
        for (Shift shift : employee.getShifts()) {
            if (previousShiftStartTime == null) {
                previousShiftStartTime = shift.getStartTime().withTimeAtStartOfDay();
                currentConsecutive = 1;
                continue;
            }

            if (shift.getStartTime().withTimeAtStartOfDay().minusDays(1).equals(previousShiftStartTime)) {
                currentConsecutive++;
            } else {
                if (currentConsecutive > maxConsecutive) {
                    maxConsecutive = currentConsecutive;
                }
                currentConsecutive = 1;
            }

            previousShiftStartTime = shift.getStartTime().withTimeAtStartOfDay();
        }

        return maxConsecutive;
    }

    private static int countWeekendsWorked(Employee employee) {
        Set<Integer> weeks = new HashSet<>();

        for (Shift shift : employee.getShifts()) {
            if (shiftIsOnWeekend(shift)) {
                weeks.add(shift.getStartTime().getWeekOfWeekyear());
            }
        }

        return weeks.size();
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
