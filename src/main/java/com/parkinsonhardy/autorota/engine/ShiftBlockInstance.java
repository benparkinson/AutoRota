package com.parkinsonhardy.autorota.engine;

import org.joda.time.DateTime;

import java.util.Objects;

public class ShiftBlockInstance {

    private ShiftBlock shiftBlock;
    private DateTime startDate;

    public ShiftBlockInstance(ShiftBlock shiftBlock, DateTime startDate) {
        this.shiftBlock = shiftBlock;
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftBlockInstance that = (ShiftBlockInstance) o;
        return shiftBlock.equals(that.shiftBlock) &&
                startDate.equals(that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftBlock, startDate);
    }
}
