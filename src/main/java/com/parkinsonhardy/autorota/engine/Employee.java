package com.parkinsonhardy.autorota.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Employee {

    private long id;
    private String name;
    private List<Shift> shifts = new ArrayList<>();
    private int priorityWeight = 0;

    public Employee(long id, String name) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
