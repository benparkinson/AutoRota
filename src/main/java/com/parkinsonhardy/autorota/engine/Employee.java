package com.parkinsonhardy.autorota.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Employee {

    private final String id;
    private final String name;
    private List<Shift> shifts = new ArrayList<>();

    public Employee(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Employee(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    public String getId() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id.equals(employee.id);
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
