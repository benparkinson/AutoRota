package com.parkinsonhardy.autorota.fileparser;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import com.parkinsonhardy.autorota.helpers.IntegerMatcher;
import com.parkinsonhardy.autorota.rules.*;
import com.parkinsonhardy.autorota.util.RotaUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseFile {

    @Test
    public void parseRotaFile() throws IOException {
        DateTimeZone.setDefault(DateTimeZone.UTC);
        String filePath = "C:\\work\\git\\AutoRota\\src\\main\\resources\\rotas\\manual_rota";
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        Map<String, ShiftDefinition> shiftDefinitionMap = new HashMap<>();
        shiftDefinitionMap.put("Day", new ShiftDefinition("Day", LocalTime.parse("08:30"), LocalTime.parse("17:00")));
        shiftDefinitionMap.put("LongDay", new ShiftDefinition("LongDay", LocalTime.parse("08:30"), LocalTime.parse("21:00")));
        shiftDefinitionMap.put("Night", new ShiftDefinition("Night", LocalTime.parse("20:30"), LocalTime.parse("09:00")));

        List<Employee> employees = new RotaFileParser().parseRotaStrings(lines, shiftDefinitionMap);
        // todo work out dates from file obv
        String s = RotaUtils.stringifyRota(employees, DateTime.parse("2019-01-07"), DateTime.parse("2019-04-01"));
        System.out.println(s);

        List<Rule> rules = new ArrayList<>();

        rules.add(new MinHoursBetweenShiftsRule(11));
        rules.add(new MaxConsecutiveShiftRule("LongDay", 5));
        rules.add(new MaxConsecutiveShiftRule("Night", 4));
        rules.add(new MaxHoursPerWeekRule(72));
        rules.add(new NoMoreThanOneConsecutiveWeekendsRule());
        rules.add(new MinHoursAfterConsecutiveShiftsRule("LongDay", 4, 48));
        rules.add(new MinHoursAfterConsecutiveShiftsRule("Night", new IntegerMatcher(3, 4), 46));

        for (Rule rule : rules) {
            for (Employee employee : employees) {
                if (!rule.shiftsPassesRule(employee.getShifts())) {
                    throw new IllegalStateException(String.format("Employee: %s breaks rule: %s", employee.getName(), rule.getName()));
                }
            }
        }

        MaxAverageHoursPerWeekRule maxAverageHoursPerWeekRule = new MaxAverageHoursPerWeekRule(48);

        if (!maxAverageHoursPerWeekRule.passesFinalCheck(employees)) {
            throw new IllegalStateException("One of more employees has too many hours per week on average!");
        }
    }

}
