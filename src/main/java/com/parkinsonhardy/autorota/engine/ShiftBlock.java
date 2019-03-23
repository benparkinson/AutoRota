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

        daysInBlock.sort(Comparator.comparingInt(DayOfWeek::getValue));

        int i = -1;
        for (DayOfWeek dayOfWeek : daysInBlock) {
            if (i >= 0) {
                if (Math.abs(dayOfWeek.getValue() - i) % 7 >= 2) {
                    throw new IllegalArgumentException("Days in block must be contiguous!");
                }
            }
            i = dayOfWeek.getValue();
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
}
