package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.controllers.parsers.RuleType;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.engine.ShiftBlock;

import java.time.DayOfWeek;
import java.util.List;

public class ShiftBlocksSoftRule extends SoftRule {

    private final ShiftBlock preferredShiftBlock;
    private final boolean mandatory;

    public ShiftBlocksSoftRule(int weight, ShiftBlock preferredShiftBlock) {
        this(weight, preferredShiftBlock, false);
    }

    public ShiftBlocksSoftRule(int weight, ShiftBlock preferredShiftBlock, boolean mandatory) {
        super(weight);

        this.preferredShiftBlock = preferredShiftBlock;
        this.mandatory= mandatory;
    }

    public ShiftBlock getPreferredShiftBlock() {
        return preferredShiftBlock;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    protected int innerCalculateScore(List<Employee> employees) {
        float totalPossibleBlocks = 0;
        float blocksFound = 0;

        for (Employee employee : employees) {
            boolean currentlyInBlock = false;
            DayOfWeek previousDay = null;
            for (Shift shift : employee.getShifts()) {
                DayOfWeek startDay = DayOfWeek.of(shift.getStartTime().getDayOfWeek());
                if (weCareAboutShift(shift) && oneDayBetweenDays(previousDay, startDay)) {
                    if (currentlyInBlock) {
                        // check we are at end of block
                        if (!preferredShiftBlock.isForNextDay(startDay)) {
                            blocksFound++;
                            currentlyInBlock = false;
                        }
                    } else {
                        // check we haven't added a shift midway through a block
                        if (!preferredShiftBlock.isForPreviousDay(startDay))
                            currentlyInBlock = true;

                        totalPossibleBlocks++;
                    }
                } else {
                    currentlyInBlock = false;
                }

                previousDay = currentlyInBlock ? startDay : null;
            }
        }

        if (totalPossibleBlocks == 0) {
            return PERFECT_SCORE;
        }

        return Math.round((blocksFound / totalPossibleBlocks) * PERFECT_SCORE);
    }

    private boolean weCareAboutShift(Shift shift) {
        return shift.getShiftType().equals(preferredShiftBlock.getShiftType()) &&
                preferredShiftBlock.isForDay(DayOfWeek.of(shift.getStartTime().getDayOfWeek()));
    }

    private boolean oneDayBetweenDays(DayOfWeek previousDay, DayOfWeek startDay) {
        return previousDay == null || startDay.getValue() == previousDay.plus(1).getValue();
    }

    @Override
    public RuleType getRuleType() {
        return RuleType.ShiftBlocks;
    }
}
