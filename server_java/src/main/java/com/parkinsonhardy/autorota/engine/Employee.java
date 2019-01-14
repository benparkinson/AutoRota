package com.parkinsonhardy.autorota.engine;

import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Employee {

    private long id;
    private String name;
    private List<Shift> shifts = new ArrayList<>();
    private float priorityWeight = 0;

    public Employee(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
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

    public void removeShift(Shift shift) {
        // todo consider different collection to make this more efficient. Needs to be ordered for rules to work ideally
        this.shifts.remove(shift);
    }

    public boolean isAvailableForShift(Shift potentialShift) {
        for (Shift shift : shifts) {
            if (shiftsOverlap(shift, potentialShift)) {
                return false;
            }
        }
        return true;
    }

    private boolean shiftsOverlap(Shift shift, Shift potentialShift) {
        Interval interval1 = new Interval(shift.getStartTime(), shift.getEndTime());
        Interval interval2 = new Interval(potentialShift.getStartTime(), potentialShift.getEndTime());
        return interval1.overlaps(interval2);
    }

    public float getPriorityWeight() {
        return priorityWeight;
    }

    public void setPriorityWeight(float priorityWeight) {
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

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", shifts=" + shifts.size() +
                '}';
    }
}
