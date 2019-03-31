package com.parkinsonhardy.autorota.rules;

import com.parkinsonhardy.autorota.controllers.parsers.RuleType;
import com.parkinsonhardy.autorota.engine.Employee;
import com.parkinsonhardy.autorota.engine.Shift;

import java.util.*;

public class ShiftTypeBalanceSoftRule extends SoftRule {

    private final Set<String> allShiftTypes;

    public ShiftTypeBalanceSoftRule(int weight, Set<String> allShiftTypes) {
        super(weight);
        this.allShiftTypes = allShiftTypes;
    }

    @Override
    protected int innerCalculateScore(List<Employee> employees) {
        if (employees.size() == 0) {
            return PERFECT_SCORE;
        }

        Map<String, List<Float>> shiftCounts = new HashMap<>();
        Map<String, Float> totalShiftCountByType = new HashMap<>();
        for (Employee employee : employees) {
            Map<String, Float> shiftCountByType = new HashMap<>();
            for (String shiftType : allShiftTypes) {
                shiftCountByType.put(shiftType, 0.0f);
            }
            for (Shift shift : employee.getShifts()) {
                String shiftType = shift.getShiftType();
                Float aDouble = totalShiftCountByType.get(shiftType);
                if (aDouble == null) {
                    aDouble = 0.0f;
                }
                totalShiftCountByType.put(shiftType, ++aDouble);

                Float count = shiftCountByType.get(shiftType);
                if (count == null) {
                    throw new IllegalArgumentException(String.format("Unexpected shift type: %s!", shiftType));
                }
                shiftCountByType.put(shiftType, ++count);
            }
            for (Map.Entry<String, Float> shiftCountForEmployee : shiftCountByType.entrySet()) {
                List<Float> counts = shiftCounts.computeIfAbsent(shiftCountForEmployee.getKey(), k -> new ArrayList<>());
                counts.add(shiftCountForEmployee.getValue());
            }
        }

        float penalty = PERFECT_SCORE / (float) employees.size();
        int score = PERFECT_SCORE;

        for (Map.Entry<String, Float> shiftTotal : totalShiftCountByType.entrySet()) {
            Float totalShifts = shiftTotal.getValue();
            Float idealProportion = totalShifts / (float) employees.size();
            List<Float> employeeCounts = shiftCounts.get(shiftTotal.getKey());
            for (Float count : employeeCounts) {
                float miss = Math.abs(count - idealProportion) / idealProportion;
                score -= (penalty * miss);
            }
        }


        return score;
    }

    @Override
    public RuleType getRuleType() {
        return RuleType.ShiftTypeBalance;
    }
}
