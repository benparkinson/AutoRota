package com.parkinsonhardy.autorota.engine;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShiftRequirement {

    private String shiftType;
    private int minEmployees;
    private Set<DayOfWeek> daysOfWeek;

    public ShiftRequirement(String shiftType, int minEmployees, DayOfWeek... daysOfWeek) {
        this(shiftType, minEmployees, Arrays.asList(daysOfWeek));
    }

    public ShiftRequirement(String shiftType, int minEmployees, List<DayOfWeek> daysOfWeek) {
        this.shiftType = shiftType;
        this.minEmployees = minEmployees;
        this.daysOfWeek = new HashSet<>();
        this.daysOfWeek.addAll(daysOfWeek);
    }

    public String getShiftType() {
        return shiftType;
    }

    public int getMinEmployees() {
        return minEmployees;
    }

    public boolean shiftRequiredOnDay(DayOfWeek dayOfWeek) {
        return daysOfWeek.contains(dayOfWeek);
    }
}
