package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.RotaEngineTestBase;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.ShiftDefinition;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;

public class OnlyOneShiftADayRuleTest extends RotaEngineTestBase {

    @Test
    public void testRulePasses() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        Employee employee = new Employee("Real Ben");

        OnlyOneShiftADayRule rule = new OnlyOneShiftADayRule();

        DateTime monday = getNextMonday();
        DateTime tuesday = monday.plusDays(1);

        addShiftToEmployee(dayShift, monday, employee);
        addShiftToEmployee(dayShift, tuesday, employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));
    }

    @Test
    public void testRuleFails() {
        ShiftDefinition dayShift = new ShiftDefinition("Day", LocalTime.parse("10:30"), LocalTime.parse("11:30"));
        Employee employee = new Employee("Real Ben");

        OnlyOneShiftADayRule rule = new OnlyOneShiftADayRule();

        DateTime monday = getNextMonday();
        DateTime tuesday = monday.plusDays(1);

        addShiftToEmployee(dayShift, monday, employee);
        addShiftToEmployee(dayShift, tuesday, employee);

        Assert.assertTrue(rule.shiftsPassesRule(employee.getShifts()));

        addShiftToEmployee(dayShift, tuesday, employee);

        Assert.assertFalse(rule.shiftsPassesRule(employee.getShifts()));
    }

}
