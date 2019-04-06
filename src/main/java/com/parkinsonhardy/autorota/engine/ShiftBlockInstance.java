package com.parkinsonhardy.autorota.engine;

import org.joda.time.DateTime;

import java.util.*;

// tree structure to represent enforced shift blocks in a rota
public class ShiftBlockInstance {

    private ShiftBlock shiftBlock;
    private DateTime startDate;
    private List<Shift> shifts;
    private Set<DateTime> datesWithShifts;
    // for where we have multiple blocks for the same set/start date (e.g. we require 2 blocks of nights for the weekend)
    private ShiftBlockInstance inner;

    public ShiftBlockInstance(ShiftBlock shiftBlock, DateTime startDate) {
        this.shiftBlock = shiftBlock;
        this.startDate = startDate;
        this.shifts = new ArrayList<>();
        this.datesWithShifts = new HashSet<>();
    }

    public void addShift(Shift shift) {
        if (datesWithShifts.contains(shift.getStartTime())) {
            ShiftBlockInstance inner = getInner();
            inner.addShift(shift);
        } else {
            shifts.add(shift);
            datesWithShifts.add(shift.getStartTime());
        }
    }

    private ShiftBlockInstance getInner() {
        if (inner != null) {
            return inner;
        }
        inner = new ShiftBlockInstance(this.shiftBlock, this.startDate);
        return inner;
    }

    public List<List<Shift>> getAllShifts() {
        List<List<Shift>> allShifts = new ArrayList<>();
        allShifts.add(shifts);
        if (inner != null) {
            allShifts.addAll(inner.getAllShifts());
        }
        return allShifts;
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
