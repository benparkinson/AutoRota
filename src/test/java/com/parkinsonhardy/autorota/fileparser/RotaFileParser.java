package com.parkinsonhardy.autorota.fileparser;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import com.parkinsonhardy.autorota.helpers.ShiftCreator;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RotaFileParser {

    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd EEE");

    public List<Employee> parseRotaStrings(List<String> rotaFileLines, Map<String, ShiftDefinition> shiftDefinitionsByType) {
        Map<Integer, Employee> employeesByColumnIndex = new HashMap<>();
        String employeeRow = rotaFileLines.get(0);
        String[] split = employeeRow.split(",");
        for (int i = 0; i < split.length; i++) {
            String employee = split[i];
            if (!"".equals(employee)) {
                employeesByColumnIndex.put(i, new Employee(employee));
            }
        }
        int shiftId = 0;

        for (int i = 1; i < rotaFileLines.size(); i++) {
            String line = rotaFileLines.get(i);

            String[] shifts = line.split(",");
            DateTime dateTime = DateTime.parse(shifts[0], formatter);
            for (int j = 1; j < shifts.length; j++) {
                String shiftType = shifts[j];
                if (!"".equals(shiftType)) {
                    ShiftDefinition shiftDefinition = shiftDefinitionsByType.get(shiftType);
                    Shift shift = ShiftCreator.createFromDefinition(shiftId++, dateTime, shiftDefinition);
                    Employee employee = employeesByColumnIndex.get(j);
                    employee.addShift(shift);
                }
            }
        }

        return Lists.newArrayList(employeesByColumnIndex.values());
    }

}
