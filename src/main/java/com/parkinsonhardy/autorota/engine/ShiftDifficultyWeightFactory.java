package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.controllers.parsers.RuleType;
import com.parkinsonhardy.autorota.rules.ShiftBlocksSoftRule;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

public class ShiftDifficultyWeightFactory implements SelectionSorterWeightFactory<RotaSolution, Shift> {

    private List<ShiftBlock> allShiftBlocks;

    @Override
    public ShiftDifficultyWeight createSorterWeight(RotaSolution rotaSolution, Shift shift) {
        // todo check if one of these gets instantiated with each rota creation, if not then can't cache this!
        if (allShiftBlocks == null) {
            allShiftBlocks = rotaSolution.getSoftRules().stream()
                    .filter(rule -> rule.getRuleType() == RuleType.ShiftBlocks)
                    .map(rule -> ((ShiftBlocksSoftRule) rule).getPreferredShiftBlock())
                    .collect(Collectors.toList());
        }

        return new ShiftDifficultyWeight(getPriorityScore(shift, allShiftBlocks), shift.getShiftId());
    }

    private int getPriorityScore(Shift shift, List<ShiftBlock> allShiftBlocks) {
        if (isOnWeekend(shift)) {
            return 2;
        }
        if (shiftInBlock(shift, allShiftBlocks)) {
            return 1;
        }
        return 0;
    }

    private boolean shiftInBlock(Shift shift, List<ShiftBlock> allShiftBlocks) {
        for (ShiftBlock shiftBlock : allShiftBlocks) {
            if (shift.getShiftType().equals(shiftBlock.getShiftType())
                    && shiftBlock.isForDay(DayOfWeek.of(shift.getStartTime().getDayOfWeek())))
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
