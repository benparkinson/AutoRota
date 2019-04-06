package com.parkinsonhardy.autorota.engine.planner;

import com.parkinsonhardy.autorota.controllers.parsers.RuleType;
import com.parkinsonhardy.autorota.engine.RotaSolution;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.engine.ShiftBlock;
import com.parkinsonhardy.autorota.engine.ShiftGroup;
import com.parkinsonhardy.autorota.engine.planner.ShiftDifficultyWeight;
import com.parkinsonhardy.autorota.rules.ShiftBlocksSoftRule;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

public class ShiftGroupDifficultyWeightFactory implements SelectionSorterWeightFactory<RotaSolution, ShiftGroup> {

    private List<ShiftBlock> allShiftBlocks;

    @Override
    public ShiftDifficultyWeight createSorterWeight(RotaSolution rotaSolution, ShiftGroup shiftGroup) {
        // todo check if one of these gets instantiated with each rota creation, if not then can't cache this!
        if (allShiftBlocks == null) {
            allShiftBlocks = rotaSolution.getSoftRules().stream()
                    .filter(rule -> rule.getRuleType() == RuleType.ShiftBlocks)
                    .map(rule -> ((ShiftBlocksSoftRule) rule).getPreferredShiftBlock())
                    .collect(Collectors.toList());
        }

        return new ShiftDifficultyWeight(getPriorityScore(shiftGroup, allShiftBlocks), shiftGroup.getId(), shiftGroup.getShiftType());
    }

    private int getPriorityScore(ShiftGroup shiftGroup, List<ShiftBlock> allShiftBlocks) {
        if (isOnWeekend(shiftGroup)) {
            return 2;
        }
        if (shiftInBlock(shiftGroup, allShiftBlocks)) {
            return 1;
        }
        return 0;
    }

    private boolean shiftInBlock(ShiftGroup shiftGroup, List<ShiftBlock> allShiftBlocks) {
        if (shiftGroup.getUnderlyingShifts().size() > 1) {
            return true;
        }

        for (ShiftBlock shiftBlock : allShiftBlocks) {
            for (Shift shift : shiftGroup.getUnderlyingShifts()) {
                if (shift.getShiftType().equals(shiftBlock.getShiftType())
                        && shiftBlock.isForDay(DayOfWeek.of(shift.getStartTime().getDayOfWeek())))
                    return true;
            }
        }

        return false;
    }

    private boolean isOnWeekend(ShiftGroup shiftGroup) {
        for (Shift shift : shiftGroup.getUnderlyingShifts()) {
            if (isOnWeekend(shift))
                return true;
        }
        return false;
    }

    private boolean isOnWeekend(Shift shift) {
        int startDay = shift.getStartTime().getDayOfWeek();
        int endDay = shift.getEndTime().getDayOfWeek();
        return startDay == DayOfWeek.SATURDAY.getValue()
                || startDay == DayOfWeek.SUNDAY.getValue()
                || endDay == DayOfWeek.SATURDAY.getValue()
                || endDay == DayOfWeek.SUNDAY.getValue();
    }
}
