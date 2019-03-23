package com.parkinsonhardy.autorota.model;

public class ShiftDefinitionArgs {

    private String shiftName;
    private String shiftStart;
    private String shiftEnd;
    private DayRequirementArgs dayRequirements;

    public ShiftDefinitionArgs(String shiftName, String shiftStart, String shiftEnd, DayRequirementArgs dayRequirements) {
        this.shiftName = shiftName;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.dayRequirements = dayRequirements;
    }

    public String getShiftName() {
        return shiftName;
    }

    public String getShiftStart() {
        return shiftStart;
    }

    public String getShiftEnd() {
        return shiftEnd;
    }

    public DayRequirementArgs getDayRequirements() {
        return dayRequirements;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public void setShiftStart(String shiftStart) {
        this.shiftStart = shiftStart;
    }

    public void setShiftEnd(String endTime) {
        this.shiftEnd = endTime;
    }

    public void setDayRequirements(DayRequirementArgs dayRequirements) {
        this.dayRequirements = dayRequirements;
    }

    @Override
    public String toString() {
        return "ShiftDefinitionArgs{" +
                "shiftName='" + shiftName + '\'' +
                ", shiftStart='" + shiftStart + '\'' +
                ", shiftEnd='" + shiftEnd + '\'' +
                ", dayRequirements=" + dayRequirements +
                '}';
    }
}
