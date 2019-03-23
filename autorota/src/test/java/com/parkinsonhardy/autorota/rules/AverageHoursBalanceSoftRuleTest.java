package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.parkinsonhardy.autorota.rules.SoftRule.PERFECT_SCORE;

public class AverageHoursBalanceSoftRuleTest {

    private long employeeId = 0;

    @Test
    public void testOneEmployeePerfectScore() {
        AverageHoursBalanceSoftRule rule = new AverageHoursBalanceSoftRule(1);
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(createEmployeeWithHours(1));

        int softScore = rule.calculateSoftScore(employeeList);

        Assert.assertEquals(PERFECT_SCORE, softScore);
    }

    @Test
    public void testTwoEmployeesUnbalancedZeroScore() {
        AverageHoursBalanceSoftRule rule = new AverageHoursBalanceSoftRule(1);
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(createEmployeeWithHours(1));
        employeeList.add(createEmployeeWithHours(0));

        int softScore = rule.calculateSoftScore(employeeList);

        Assert.assertEquals(0, softScore);
    }

    @Test
    public void testTwoEmployeesBalancedPerfectScore() {
        AverageHoursBalanceSoftRule rule = new AverageHoursBalanceSoftRule(1);
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(createEmployeeWithHours(1));
        employeeList.add(createEmployeeWithHours(1));

        int softScore = rule.calculateSoftScore(employeeList);

        Assert.assertEquals(PERFECT_SCORE, softScore);
    }

    @Test
    public void testTwoEmployeesMixedOkScore() {
        AverageHoursBalanceSoftRule rule = new AverageHoursBalanceSoftRule(1);
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(createEmployeeWithHours(3));
        employeeList.add(createEmployeeWithHours(1));

        int softScore = rule.calculateSoftScore(employeeList);

        Assert.assertEquals(PERFECT_SCORE / 2, softScore);
    }

    @Test
    public void testWeight() {
        AverageHoursBalanceSoftRule rule = new AverageHoursBalanceSoftRule(2);
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(createEmployeeWithHours(1));
        employeeList.add(createEmployeeWithHours(1));

        int softScore = rule.calculateSoftScore(employeeList);

        Assert.assertEquals(PERFECT_SCORE * 2, softScore);
    }

    private int shiftCount = 0;

    private Employee createEmployeeWithHours(int hours) {
        Employee employee = new Employee(String.valueOf(employeeId));
        Shift hourShift = new Shift(shiftCount++, "Day", DateTime.now().withTimeAtStartOfDay(), DateTime.now().withTimeAtStartOfDay().plusHours(1));
        for (int i = 0; i < hours; i++) {
            employee.addShift(hourShift);
        }
        return employee;
    }

}
