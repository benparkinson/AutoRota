package com.parkinsonhardy.autorota.engine;

import java.time.DayOfWeek;
import java.util.*;

public class ShiftBlock {

    private final String shiftType;
    private final Set<DayOfWeek> daysInBlockSet;

    public ShiftBlock(String shiftType, DayOfWeek... daysInBlock) {
        this(shiftType, Arrays.asList(daysInBlock));
    }

    public ShiftBlock(String shiftType, List<DayOfWeek> daysInBlock) {
        validateDays(daysInBlock);

        this.shiftType = shiftType;
        this.daysInBlockSet = new HashSet<>();
        this.daysInBlockSet.addAll(daysInBlock);
    }

    private void validateDays(List<DayOfWeek> daysInBlock) {
        if (daysInBlock.size() <= 1) {
            throw new IllegalArgumentException("Shift blocks must be at least two days long");
        }

        if (daysInBlock.size() == 7) {
            throw new IllegalArgumentException("Shift blocks for every day not yet supported!");
        }

        DayOfWeek previousDayOfWeek = daysInBlock.get(0);
        for (int i = 1; i < daysInBlock.size(); i++) {
            DayOfWeek dayOfWeek = daysInBlock.get(i);
            if (previousDayOfWeek.plus(1) != dayOfWeek)
                throw new IllegalArgumentException("Days in block must be contiguous!");

            previousDayOfWeek = dayOfWeek;
        }
    }

    public String getShiftType() {
        return shiftType;
    }

    public boolean isForDay(DayOfWeek dayOfWeek) {
        return daysInBlockSet.contains(dayOfWeek);
    }

    public boolean isForNextDay(DayOfWeek dayOfWeek) {
        return daysInBlockSet.contains(dayOfWeek.plus(1));
    }

    public boolean isForPreviousDay(DayOfWeek dayOfWeek) {
        return daysInBlockSet.contains(dayOfWeek.minus(1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftBlock that = (ShiftBlock) o;
        return shiftType.equals(that.shiftType) &&
                daysInBlockSet.equals(that.daysInBlockSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftType, daysInBlockSet);
    }
}
