package com.parkinsonhardy.autorota.engine;

import org.joda.time.DateTime;

import java.util.Objects;

public class Shift implements Comparable<Shift> {

    private int shiftId;
    private String shiftType;
    private DateTime startTime;
    private DateTime endTime;

    public Shift(int shiftId, String shiftType, DateTime startTime, DateTime endTime) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("Shift's end time is before start time!");
        }

        this.shiftId = shiftId;
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

    public int getShiftId() {
        return shiftId;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "shiftId='" + shiftId + '\'' +
                ", shiftType='" + shiftType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return this.shiftId == shift.shiftId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftId);
    }
}
