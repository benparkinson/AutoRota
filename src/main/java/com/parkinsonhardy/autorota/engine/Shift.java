package com.parkinsonhardy.autorota.engine;

import org.joda.time.DateTime;

import java.util.Objects;
import java.util.UUID;

public class Shift implements Comparable<Shift> {

    private final String shiftId;
    private final String shiftType;
    private final DateTime startTime;
    private final DateTime endTime;

    public Shift(String shiftType, DateTime startTime, DateTime endTime) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("Shift's end time is before start time!");
        }

        this.shiftId = UUID.randomUUID().toString();
        this.shiftType = shiftType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getShiftType() {
        return shiftType;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public int compareTo(Shift o) {
        return startTime.compareTo(o.startTime);
    }

    @Override
    public String toString() {
        return "Shift{" +
                "shiftType='" + shiftType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return shiftId.equals(shift.shiftId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftId);
    }
}
