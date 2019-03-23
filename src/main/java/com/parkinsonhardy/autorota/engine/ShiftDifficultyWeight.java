package com.parkinsonhardy.autorota.engine;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class ShiftDifficultyWeight implements Comparable<ShiftDifficultyWeight> {

    private final int difficulty;
    private final int shiftId;

    public ShiftDifficultyWeight(int difficulty, int shiftId) {
        this.difficulty = difficulty;
        this.shiftId = shiftId;
    }

    @Override
    public int compareTo(ShiftDifficultyWeight o) {
        return new CompareToBuilder()
                .append(this.difficulty, o.difficulty)
                // currently sorting is a bit arbitrary, if you switch these IDs around it can't generate a feasible first pass
                // should maybe also group by weekend, or something else to be more predictable
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