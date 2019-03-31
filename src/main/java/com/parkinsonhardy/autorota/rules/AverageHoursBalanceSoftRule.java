package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.controllers.parsers.RuleType;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;
import com.parkinsonhardy.autorota.helpers.ShiftHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AverageHoursBalanceSoftRule extends SoftRule {

    public AverageHoursBalanceSoftRule(int weight) {
        super(weight);
    }

    @Override
    protected int innerCalculateScore(List<Employee> employees) {
        if (employees.size() == 0) {
            return PERFECT_SCORE;
        }

        float totalShiftHours = 0;
        Map<Employee, Float> hoursByEmployee = new HashMap<>();
        for (Employee employee : employees) {
            float hoursForEmployee = 0;
            for (Shift shift : employee.getShifts()) {
                float hours = ShiftHelper.calculateShiftHours(shift);
                totalShiftHours += hours;
                hoursForEmployee += hours;
            }

            hoursByEmployee.put(employee, hoursForEmployee);
        }

        Float idealProportion = totalShiftHours / (float) employees.size();
        float penalty = PERFECT_SCORE / (float) employees.size();
        int score = PERFECT_SCORE;

        for (Map.Entry<Employee, Float> entry : hoursByEmployee.entrySet()) {
            if (!idealProportion.equals(entry.getValue())) {
                float miss = Math.abs(entry.getValue() - idealProportion) / idealProportion;
                score -= (penalty * miss);
            }
        }

        return score;
    }

    @Override
    public RuleType getRuleType() {
        return RuleType.AverageHoursBalance;
    }
}
