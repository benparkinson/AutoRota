package com.parkinsonhardy.autorota.engine;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class ShiftDifficultyWeight implements Comparable<ShiftDifficultyWeight> {

    private final int difficulty;
    private final int shiftId;
    private final String shiftType;

    public ShiftDifficultyWeight(int difficulty, int shiftId, String shiftType) {
        this.difficulty = difficulty;
        this.shiftId = shiftId;
        this.shiftType = shiftType;
    }

    @Override
    public int compareTo(ShiftDifficultyWeight o) {
        return new CompareToBuilder()
                .append(this.difficulty, o.difficulty)
                // currently sorting is a bit arbitrary, if you switch these IDs around it can't generate a feasible first pass
                // should maybe also group by weekend, or something else to be more predictable
                .append(o.shiftType, this.shiftType)
                .append(o.shiftId, this.shiftId)
                .toComparison();
    }

    public int getShiftId() {
        return shiftId;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
