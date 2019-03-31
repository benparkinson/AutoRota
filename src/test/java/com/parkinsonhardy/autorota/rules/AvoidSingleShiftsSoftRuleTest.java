package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.parkinsonhardy.autorota.rules.SoftRule.PERFECT_SCORE;

public class AvoidSingleShiftsSoftRuleTest extends RotaEngineTestBase {

    @Test
    public void testNoShiftsPerfectScore() {
        AvoidSingleShiftsSoftRule rule = new AvoidSingleShiftsSoftRule(1);
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(new Employee("Test"));

        int softScore = rule.calculateSoftScore(employeeList);

        Assert.assertEquals(PERFECT_SCORE, softScore);
    }

    @Test
    public void testOneSingleShiftZeroScore() {
        AvoidSingleShiftsSoftRule rule = new AvoidSingleShiftsSoftRule(1);
        List<Employee> employeeList = new ArrayList<>();
        Employee employee = new Employee("Test");
        employee.addShift(new Shift(1, "Day", getNextMonday(), getNextMonday()));
        employeeList.add(employee);

        int softScore = rule.calculateSoftScore(employeeList);

        Assert.assertEquals(0, softScore);
    }

    @Test
    public void testTwoShiftsTogetherPerfectScore() {
        AvoidSingleShiftsSoftRule rule = new AvoidSingleShiftsSoftRule(1);
        List<Employee> employeeList = new ArrayList<>();
        Employee employee = new Employee("Test");
        employee.addShift(new Shift(1, "Day", getNextMonday(), getNextMonday()));
        employee.addShift(new Shift(2, "Day", getNextMonday().plusDays(1), getNextMonday().plusDays(1)));
        employeeList.add(employee);

        int softScore = rule.calculateSoftScore(employeeList);

        Assert.assertEquals(PERFECT_SCORE, softScore);
    }

    @Test
    public void testTwoShiftsApartZeroScore() {
        AvoidSingleShiftsSoftRule rule = new AvoidSingleShiftsSoftRule(1);
        List<Employee> employeeList = new ArrayList<>();
        Employee employee = new Employee("Test");
        employee.addShift(new Shift(1, "Day", getNextMonday(), getNextMonday()));
        employee.addShift(new Shift(2, "Day", getNextMonday().plusDays(2), getNextMonday().plusDays(2)));
        employeeList.add(employee);

        int softScore = rule.calculateSoftScore(employeeList);

        Assert.assertEquals(0, softScore);
    }
}
