package com.parkinsonhardy.autorota.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RuleController {

    @GetMapping("/api/rules/hard/get")
    public String getHardRules() {
        return "[" +
                "    { \"niceName\": \"\", \"name\": \"\", \"unique\": false, \"params\": [] },\n" +
                "    { \"niceName\": \"Minimum Hours Between Shifts\", \"name\": \"MinHoursBetweenShifts\", \"unique\": true, \"params\": [{ \"name\": \"MinHours\", \"type\": \"number\" }] },\n" +
                "    { \"niceName\": \"Maximum Average Hours Per Week\", \"name\": \"MaxAverageHoursPerWeek\", \"unique\": true, \"params\": [{ \"name\": \"MaxHours\", \"type\": \"number\" }] },\n" +
                "    { \"niceName\": \"Maximum Consecutive Shifts\", \"name\": \"MaxConsecutiveShifts\", \"unique\": false, \"params\": [{ \"name\": \"ShiftName\", \"type\": \"text\" }, { \"name\": \"MaxConsecutive\", \"type\": \"number\" }] },\n" +
                "    { \"niceName\": \"Maximum Hours Per Week\", \"name\": \"MaxHoursPerWeek\", \"unique\": true, \"params\": [{ \"name\": \"MaxHours\", \"type\": \"number\" }] },\n" +
                "    { \"niceName\": \"No More Than One Weekend In a Row\", \"name\": \"NoMoreThanOneWeekendInARow\", \"unique\": true, \"params\": [] },\n" +
                "    { \"niceName\": \"Minimum Hours Break After Consecutive Shifts\", \"name\": \"MinHoursBreakAfterConsecutiveShifts\", \"unique\": false, \"params\": [{ \"name\": \"ShiftName\", \"type\": \"text\" }, { \"name\": \"MaxConsecutive\", \"type\": \"text\" }, { \"name\": \"MinHours\", \"type\": \"number\" }] }\n" +
                "    ]";
    }

    @GetMapping("/api/rules/soft/get")
    public String getSoftRules() {
        return "[\n" +
                "  {\n" +
                "    \"niceName\": \"\"," +
                "    \"name\": \"\",\n" +
                "    \"unique\": false,\n" +
                "    \"params\": []\n" +
                "  },\n" +
                "  {\n" +
                "    \"niceName\": \"Balance of Average Hours across Doctors\",\n" +
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
                "    \"niceName\": \"Allocate Shifts in Blocks\",\n" +
                "    \"name\": \"ShiftBlocks\",\n" +
                "    \"unique\": false,\n" +
                "    \"params\": [\n" +
                "      {\n" +
                "        \"name\": \"ShiftName\",\n" +
                "        \"type\": \"text\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"DaysInBlock\",\n" +
                "        \"type\": \"text\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Weight\",\n" +
                "        \"type\": \"number\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"niceName\": \"Balance of Shift Types across Doctors\",\n"+
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
