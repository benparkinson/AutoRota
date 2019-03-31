package com.parkinsonhardy.autorota.engine;

import org.joda.time.DateTime;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@PlanningEntity(difficultyWeightFactoryClass = ShiftGroupDifficultyWeightFactory.class)
public class ShiftGroup implements Comparable<ShiftGroup> {

    private int id;
    private String shiftType;

    public DateTime getStartTime() {
        return startTime;
    }

    private DateTime startTime;
    private List<Shift> underlyingShifts;
    private Employee employee;

    // for planning framework
    public ShiftGroup() {
        underlyingShifts = new ArrayList<>();
    }

    public ShiftGroup(Shift underlyingSingleShift) {
        this();
        this.id = underlyingSingleShift.getShiftId();
        this.underlyingShifts.add(underlyingSingleShift);
        this.shiftType = underlyingSingleShift.getShiftType();
        this.startTime = underlyingSingleShift.getStartTime();
    }

    public ShiftGroup(List<Shift> underlyingShifts) {
        this();
        this.underlyingShifts.addAll(underlyingShifts);
        this.underlyingShifts.sort(Shift::compareTo);
        this.id = this.underlyingShifts.get(0).getShiftId();
        this.shiftType = this.underlyingShifts.get(0).getShiftType();
        this.startTime = this.underlyingShifts.get(0).getStartTime();
    }

    public int getId() {
        return id;
    }

    public List<Shift> getUnderlyingShifts() {
        return underlyingShifts;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        for (Shift shift : underlyingShifts) {
            shift.setEmployee(employee);
        }
    }

    @PlanningVariable(valueRangeProviderRefs = {"employees"})
    public Employee getEmployee() {
        return employee;
    }

    @Override
    public int compareTo(ShiftGroup o) {
        return underlyingShifts.get(0).getStartTime().compareTo(o.underlyingShifts.get(0).getStartTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftGroup that = (ShiftGroup) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getShiftType() {
        return shiftType;
    }
}
