package com.parkinsonhardy.autorota.engine;

import org.joda.time.LocalTime;

import java.util.Objects;

public class ShiftDefinition {

    private String shiftType;
    private LocalTime startTime;
    private LocalTime endTime;

    public ShiftDefinition(String shiftType, LocalTime startTime, LocalTime endTime) {
        this.shiftType = shiftType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getShiftType() {
        return shiftType;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftDefinition that = (ShiftDefinition) o;
        return Objects.equals(shiftType, that.shiftType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftType);
    }
}
