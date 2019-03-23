package com.parkinsonhardy.autorota.controllers.parsers;

import com.parkinsonhardy.autorota.engine.RotaEngine;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import com.parkinsonhardy.autorota.engine.ShiftRequirement;
import com.parkinsonhardy.autorota.model.ShiftDefinitionArgs;
import org.joda.time.LocalTime;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public class ShiftCreator {

    public void addShiftDefinition(RotaEngine rotaEngine, ShiftDefinitionArgs args) {
        String shiftName = args.getShiftName();
        LocalTime start = LocalTime.parse(args.getShiftStart());
        LocalTime end = LocalTime.parse(args.getShiftEnd());

        ShiftDefinition shiftDefinition = new ShiftDefinition(shiftName, start, end);
        rotaEngine.addShiftDefinition(shiftDefinition);

        Map<Integer, List<DayOfWeek>> requirementsGroupedByCount = args.getDayRequirements().getRequirementsGroupedByCount();

        for (Map.Entry<Integer, List<DayOfWeek>> requirement : requirementsGroupedByCount.entrySet()) {
            ShiftRequirement shiftRequirement = new ShiftRequirement(shiftName, requirement.getKey(), requirement.getValue());
            rotaEngine.addShiftRequirement(shiftRequirement);
        }
    }
}
