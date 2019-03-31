package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.engine.ShiftBlock;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static com.parkinsonhardy.autorota.rules.SoftRule.PERFECT_SCORE;
import static java.time.DayOfWeek.*;

public class ShiftBlocksSoftRuleTest extends RotaEngineTestBase {

    private long employeeId = 0;

    @Test
    public void testNoShiftsPerfectScore() {
        Employee e = new Employee("Test");

        List<DayOfWeek> daysForBlock = new ArrayList<>();
        daysForBlock.add(FRIDAY);
        daysForBlock.add(SATURDAY);
        daysForBlock.add(SUNDAY);
        ShiftBlock block = new ShiftBlock("Test", daysForBlock);
        ShiftBlocksSoftRule rule = new ShiftBlocksSoftRule(1, block);

        List<Employee> employees = new ArrayList<>();
        employees.add(e);
        int i = rule.innerCalculateScore(employees);

        Assert.assertEquals(PERFECT_SCORE, i);
    }

    @Test
    public void testSimpleBlock() {
        Employee e = createEmployee(1, "Test", FRIDAY, SATURDAY, SUNDAY);

        List<DayOfWeek> daysForBlock = new ArrayList<>();
        daysForBlock.add(FRIDAY);
        daysForBlock.add(SATURDAY);
        daysForBlock.add(SUNDAY);
        ShiftBlock block = new ShiftBlock("Test", daysForBlock);
        ShiftBlocksSoftRule rule = new ShiftBlocksSoftRule(1, block);

        List<Employee> employees = new ArrayList<>();
        employees.add(e);
        int i = rule.innerCalculateScore(employees);

        Assert.assertEquals(PERFECT_SCORE, i);
    }

    @Test
    public void testSimpleBlockExpectBadResult() {
        Employee e = createEmployee(1, "Test", FRIDAY, SUNDAY);

        List<DayOfWeek> daysForBlock = new ArrayList<>();
        daysForBlock.add(FRIDAY);
        daysForBlock.add(SATURDAY);
        daysForBlock.add(SUNDAY);
        ShiftBlock block = new ShiftBlock("Test", daysForBlock);
        ShiftBlocksSoftRule rule = new ShiftBlocksSoftRule(1, block);

        List<Employee> employees = new ArrayList<>();
        employees.add(e);
        int i = rule.innerCalculateScore(employees);

        Assert.assertEquals(0, i);
    }

    @Test
    public void testSimpleBlockExpectBadResultMissingStart() {
        Employee e = createEmployee(1, "Test", SATURDAY, SUNDAY);

        List<DayOfWeek> daysForBlock = new ArrayList<>();
        daysForBlock.add(FRIDAY);
        daysForBlock.add(SATURDAY);
        daysForBlock.add(SUNDAY);
        ShiftBlock block = new ShiftBlock("Test", daysForBlock);
        ShiftBlocksSoftRule rule = new ShiftBlocksSoftRule(1, block);

        List<Employee> employees = new ArrayList<>();
        employees.add(e);
        int i = rule.innerCalculateScore(employees);

        Assert.assertEquals(0, i);
    }

    @Test
    public void testSimpleBlockExpectBadResultOnlyLastDay() {
        Employee e = createEmployee(1, "Test", SUNDAY);

        List<DayOfWeek> daysForBlock = new ArrayList<>();
        daysForBlock.add(FRIDAY);
        daysForBlock.add(SATURDAY);
        daysForBlock.add(SUNDAY);
        ShiftBlock block = new ShiftBlock("Test", daysForBlock);
        ShiftBlocksSoftRule rule = new ShiftBlocksSoftRule(1, block);

        List<Employee> employees = new ArrayList<>();
        employees.add(e);
        int i = rule.innerCalculateScore(employees);

        Assert.assertEquals(0, i);
    }

    @Test
    public void testSimpleBlockExpectBadResultMissingEnd() {
        Employee e = createEmployee(1, "Test", FRIDAY, SATURDAY);

        List<DayOfWeek> daysForBlock = new ArrayList<>();
        daysForBlock.add(FRIDAY);
        daysForBlock.add(SATURDAY);
        daysForBlock.add(SUNDAY);
        ShiftBlock block = new ShiftBlock("Test", daysForBlock);
        ShiftBlocksSoftRule rule = new ShiftBlocksSoftRule(1, block);

        List<Employee> employees = new ArrayList<>();
        employees.add(e);
        int i = rule.innerCalculateScore(employees);

        Assert.assertEquals(0, i);
    }

    @Test
    public void testSimpleBlockExpectHalfResult() {
        Employee e = createEmployee(1, "Test", FRIDAY, SATURDAY, SUNDAY);
        addShifts(e, 1, "Test", FRIDAY, SATURDAY);

        List<DayOfWeek> daysForBlock = new ArrayList<>();
        daysForBlock.add(FRIDAY);
        daysForBlock.add(SATURDAY);
        daysForBlock.add(SUNDAY);
        ShiftBlock block = new ShiftBlock("Test", daysForBlock);
        ShiftBlocksSoftRule rule = new ShiftBlocksSoftRule(1, block);

        List<Employee> employees = new ArrayList<>();
        employees.add(e);
        int i = rule.innerCalculateScore(employees);

        Assert.assertEquals(PERFECT_SCORE / 2, i);
    }

    private int shiftCount = 0;

    private Employee createEmployee(int weekCount, String shiftType, DayOfWeek... daysWithShifts) {
        Employee employee = new Employee(String.valueOf(employeeId));
        DateTime monday = getNextMonday();

        int add = 0;
        for (int i = 0; i < weekCount; i++) {
            for (DayOfWeek day : daysWithShifts) {
                DateTime date = monday.plusDays(day.getValue() - 1 + add);
                employee.addShift(new Shift(shiftCount++, shiftType, date, date.plusHours(1)));
            }
            add = 7;
        }

        return employee;
    }

    private void addShifts(Employee employee, int week, String shiftType, DayOfWeek... daysWithShifts) {
        DateTime monday = getNextMonday();

        int add = week * 7;
        for (DayOfWeek day : daysWithShifts) {
            DateTime date = monday.plusDays(day.getValue() - 1 + add);
            employee.addShift(new Shift(shiftCount++, shiftType, date, date.plusHours(1)));
        }
    }
}
