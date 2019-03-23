package com.parkinsonhardy.autorota;

import com.parkinsonhardy.autorota.engine.*;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import org.joda.time.DateTime;
import org.junit.Assert;

import java.util.List;

import static java.time.DayOfWeek.*;

public class RotaEngineTestBase {

    protected RotaEngine rotaEngine;

    protected RotaEngine getRotaEngine() {
        return new TestPlannerRotaEngine();
    }

    protected void addSingleEmployee() throws RotaException {
        rotaEngine.addEmployee(new Employee("Real Ben"));
    }

    protected void addTwoEmployees() throws RotaException {
        addSingleEmployee();
        rotaEngine.addEmployee(new Employee("Shit Ben"));
    }

    protected void checkSingleEmployeeHasShiftCount(List<Employee> employees, int shiftCount) {
        checkAllEmployeeHaveShiftCount(employees, shiftCount, 1);
    }

    protected void checkAllEmployeeHaveShiftCount(List<Employee> employees, int shiftCount, int numEmployees) {
        Assert.assertEquals(numEmployees, employees.size());
        for (Employee e : employees) {
            Assert.assertEquals(shiftCount, e.getShifts().size());
        }
    }

    protected DateTime getNextMonday() {
        DateTime today = DateTime.now().withTimeAtStartOfDay();
        int dayOfWeek = today.getDayOfWeek();
        return today.plusDays(8 - dayOfWeek);
    }

    protected void addShiftRequirementForEveryDay(RotaEngine rotaEngine, String shiftType, int minEmployees) {
        rotaEngine.addShiftRequirement(new ShiftRequirement(shiftType, minEmployees, MONDAY, TUESDAY, WEDNESDAY,
                THURSDAY, FRIDAY, SATURDAY, SUNDAY));
    }

    protected void addShiftToSingleEmployee(ShiftDefinition shiftDefinition, DateTime dateTime) {
        addShiftToEmployee(shiftDefinition, dateTime, rotaEngine.getEmployees().get(0));
    }

    private int shiftCount = 0;

    protected void addShiftToEmployee(ShiftDefinition shiftDefinition, DateTime dateTime, Employee employee) {
        DateTime endTime = shiftDefinition.getStartTime().isBefore(shiftDefinition.getEndTime()) ?
                dateTime.withTime(shiftDefinition.getEndTime()) :
                dateTime.plusDays(1).withTime(shiftDefinition.getEndTime());

        employee.addShift(new Shift(shiftCount++, shiftDefinition.getShiftType(),
                dateTime.withTime(shiftDefinition.getStartTime()), endTime));
    }

}
