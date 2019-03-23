package com.parkinsonhardy.autorota.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RuleController {

    @GetMapping("/api/rules/hard/get")
    public String getHardRules() {
        return "[" +
                "    { \"name\": \"\", \"unique\": false, \"params\": [] },\n" +
                "    { \"name\": \"MinHoursBetweenShifts\", \"unique\": true, \"params\": [{ \"name\": \"MinHours\", \"type\": \"number\" }] },\n" +
                "    { \"name\": \"MaxAverageHoursPerWeek\", \"unique\": true, \"params\": [{ \"name\": \"MaxHours\", \"type\": \"number\" }] },\n" +
                "    { \"name\": \"MaxConsecutiveShifts\", \"unique\": false, \"params\": [{ \"name\": \"ShiftName\", \"type\": \"string\" }, { \"name\": \"MaxConsecutive\", \"type\": \"number\" }] },\n" +
                "    { \"name\": \"MaxHoursPerWeek\", \"unique\": true, \"params\": [{ \"name\": \"MaxHours\", \"type\": \"number\" }] },\n" +
                "    { \"name\": \"NoMoreThanOneWeekendInARow\", \"unique\": true, \"params\": [] },\n" +
                "    { \"name\": \"MinHoursBreakAfterConsecutiveShifts\", \"unique\": false, \"params\": [{ \"name\": \"ShiftName\", \"type\": \"string\" }, { \"name\": \"MaxConsecutive\", \"type\": \"string\" }, { \"name\": \"MinHours\", \"type\": \"number\" }] }\n" +
                "    ]";
    }

    @GetMapping("/api/rules/soft/get")
    public String getSoftRules() {
        return "[\n" +
                "  {\n" +
                "    \"name\": \"\",\n" +
                "    \"unique\": false,\n" +
                "    \"params\": []\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"AverageHoursBalance\",\n" +
                "    \"unique\": true,\n" +
                "    \"params\": [\n" +
                "      {\n" +
                "        \"name\": \"Weight\",\n" +
                "        \"type\": \"number\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ShiftBlocks\",\n" +
                "    \"unique\": false,\n" +
                "    \"params\": [\n" +
                "      {\n" +
                "        \"name\": \"ShiftName\",\n" +
                "        \"type\": \"string\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"DaysInBlock\",\n" +
                "        \"type\": \"string\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Weight\",\n" +
                "        \"type\": \"number\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ShiftTypeBalance\",\n" +
                "    \"unique\": true,\n" +
                "    \"params\": [\n" +
                "      {\n" +
                "        \"name\": \"Weight\",\n" +
                "        \"type\": \"number\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]";
    }


}
