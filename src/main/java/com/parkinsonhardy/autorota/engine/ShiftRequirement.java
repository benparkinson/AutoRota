package com.parkinsonhardy.autorota.engine;

public class ShiftRequirement {

    private String shiftType;
    private int minEmployees;
    private int dayOfWeek;

    public ShiftRequirement(String shiftType, int minEmployees, int dayOfWeek) {
        this.shiftType = shiftType;
        this.minEmployees = minEmployees;
        this.dayOfWeek = dayOfWeek;
    }

    public String getShiftType() {
        return shiftType;
    }

    public int getMinEmployees() {
        return minEmployees;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }
}
