package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.parkinsonhardy.autorota.rules.SoftRule.PERFECT_SCORE;

public class ShiftTypeBalanceSoftRuleTest extends RotaEngineTestBase {

    @Test
    public void testNoShiftsPerfectScore() {
        Set<String> allShiftTypes = new HashSet<>();
        allShiftTypes.add("Day");
        ShiftTypeBalanceSoftRule rule = new ShiftTypeBalanceSoftRule(1, allShiftTypes);
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(new Employee("Test"));

        int softScore = rule.calculateSoftScore(employeeList);

        Assert.assertEquals(PERFECT_SCORE, softScore);
    }

    @Test
    public void testTwoShiftsTwoEmployeesZeroScore() {
        Set<String> allShiftTypes = new HashSet<>();
        allShiftTypes.add("Day");
        ShiftTypeBalanceSoftRule rule = new ShiftTypeBalanceSoftRule(1, allShiftTypes);
        List<Employee> employeeList = new ArrayList<>();
        Employee employee = new Employee("Test");
        employee.addShift(new Shift(1, "Day", getNextMonday(), getNextMonday()));
        employee.addShift(new Shift(2, "Day", getNextMonday().plusDays(1), getNextMonday().plusDays(1)));
        employeeList.add(employee);
        employeeList.add(new Employee("Test2"));

        int softScore = rule.calculateSoftScore(employeeList);

        Assert.assertEquals(0, softScore);
    }

    @Test
    public void testTwoShiftsSharedPerfectScore() {
        Set<String> allShiftTypes = new HashSet<>();
        allShiftTypes.add("Day");
        ShiftTypeBalanceSoftRule rule = new ShiftTypeBalanceSoftRule(1, allShiftTypes);
        List<Employee> employeeList = new ArrayList<>();
        Employee employee = new Employee("Test");
        employee.addShift(new Shift(1, "Day", getNextMonday(), getNextMonday()));
        employeeList.add(employee);
        Employee employee2 = new Employee("Test2");
        employee2.addShift(new Shift(2, "Day", getNextMonday().plusDays(1), getNextMonday().plusDays(1)));
        employeeList.add(employee2);

        int softScore = rule.calculateSoftScore(employeeList);

        Assert.assertEquals(PERFECT_SCORE, softScore);
    }

}
