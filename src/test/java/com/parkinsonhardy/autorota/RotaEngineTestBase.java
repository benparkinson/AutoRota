package com.parkinsonhardy.autorota;

import com.parkinsonhardy.autorota.engine.*;
import com.parkinsonhardy.autorota.exceptions.RotaException;
import org.joda.time.DateTime;
import org.junit.Assert;

import java.util.List;

import static java.time.DayOfWeek.*;

public class RotaEngineTestBase {

    protected RotaEngine rotaEngine;

    protected void addSingleEmployee() throws RotaException {
        rotaEngine.addEmployee(new Employee(1, "Real Ben"));
    }

    protected void addTwoEmployees() throws RotaException {
        addSingleEmployee();
        rotaEngine.addEmployee(new Employee(2, "Shit Ben"));
    }

    protected void checkSingleEmployeeHasShiftCount(List<Employee> employees, int i) {
        Assert.assertEquals(1, employees.size());
        Assert.assertEquals(i, employees.get(0).getShifts().size());
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

    protected void addShiftToEmployee(ShiftDefinition shiftDefinition, DateTime dateTime,  Employee employee) {
        DateTime endTime = shiftDefinition.getStartTime().isBefore(shiftDefinition.getEndTime()) ?
                dateTime.withTime(shiftDefinition.getEndTime()) :
                dateTime.plusDays(1).withTime(shiftDefinition.getEndTime());

        employee.addShift(new Shift(shiftDefinition.getShiftType(),
                dateTime.withTime(shiftDefinition.getStartTime()), endTime));
    }

}
