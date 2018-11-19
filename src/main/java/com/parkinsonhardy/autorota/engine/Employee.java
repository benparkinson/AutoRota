package com.parkinsonhardy.autorota.engine;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    private String name;
    private List<Shift> shifts = new ArrayList<>();
    private int priorityWeight = 0;

    public Employee(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Shift> getShifts() {
        return shifts;
    }

    public void addShift(Shift shift) {
        this.shifts.add(shift);
    }

    public boolean isAvailableForShift(Shift potentialShift) {
        // todo order shifts? should be more efficient
        for (Shift shift : shifts) {
            if (shiftsOverlap(shift, potentialShift)) {
                return false;
            }
        }
        return true;
    }

    private boolean shiftsOverlap(Shift shift, Shift potentialShift) {
        return shift.equals(potentialShift) || (potentialShift.getStartTime().isBefore(shift.getEndTime())
        && potentialShift.getEndTime().isAfter(shift.getEndTime()))
                || (potentialShift.getEndTime().isAfter(shift.getStartTime())
        && potentialShift.getStartTime().isBefore(shift.getStartTime()));
    }

    public int getPriorityWeight() {
        return priorityWeight;
    }

    public void setPriorityWeight(int priorityWeight) {
        this.priorityWeight = priorityWeight;
    }
}
