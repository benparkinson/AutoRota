package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.rules.Rule;
import com.parkinsonhardy.autorota.rules.SoftRule;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RotaEasyScoreCalculator implements EasyScoreCalculator<RotaSolution> {

    private List<Rule> rules;
    private List<SoftRule> softRules;

    @Override
    public Score calculateScore(RotaSolution rotaSolution) {
        if (rules == null) {
            rules = rotaSolution.getRules();
            softRules = rotaSolution.getSoftRules();
        }

        int hardScore = 0;
        int softScore = 0;

        List<Shift> shifts = rotaSolution.getShiftGroups().stream()
                .flatMap(group -> group.getUnderlyingShifts().stream())
                .collect(Collectors.toList());
        Map<Employee, List<Shift>> shiftsByEmployee = new HashMap<>();

        for (Employee employee : rotaSolution.getEmployees()) {
            shiftsByEmployee.put(employee, new ArrayList<>());
        }

        for (Shift shift : shifts) {
            if (shift.getEmployee() != null) {
                List<Shift> shiftsForEmployee = shiftsByEmployee.get(shift.getEmployee());
                shiftsForEmployee.add(shift);
            }
        }

        for (List<Shift> shiftList : shiftsByEmployee.values()) {
            shiftList.sort(Shift::compareTo);
            for (Rule rule : rules) {
                if (!rule.shiftsPassesRule(shiftList)) {
                    hardScore = -1;
                    break;
                }
            }
            if (hardScore < 0) {
                break;
            }
        }

        if (hardScore >= 0) {
            List<Employee> employeesToCheck = new ArrayList<>();
            for (Map.Entry<Employee, List<Shift>> shiftsForEmployee : shiftsByEmployee.entrySet()) {
                Employee toAdd = new Employee(shiftsForEmployee.getKey().getId(), shiftsForEmployee.getKey().getName());
                for (Shift shift : shiftsForEmployee.getValue()) {
                    toAdd.addShift(shift);
                }
                employeesToCheck.add(toAdd);
            }
            for (SoftRule softRule : softRules) {
                softScore += softRule.calculateSoftScore(employeesToCheck);
            }
        }

        return HardSoftScore.of(hardScore, softScore);
    }
}
