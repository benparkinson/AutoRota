package com.parkinsonhardy.autorota.controllers.parsers;

import com.parkinsonhardy.autorota.engine.RotaEngine;
import com.parkinsonhardy.autorota.engine.ShiftBlock;
import com.parkinsonhardy.autorota.helpers.IntegerMatcher;
import com.parkinsonhardy.autorota.model.RuleArgs;
import com.parkinsonhardy.autorota.model.RuleParamArgs;
import com.parkinsonhardy.autorota.rules.*;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

public class RuleFactory {

    private final Set<String> allShiftTypes;
    private Map<RuleType, HolisticRuleCreator> holisticRuleCreators = new HashMap<>();
    private Map<RuleType, RuleCreator> ruleCreators = new HashMap<>();
    private Map<RuleType, SoftRuleCreator> softRuleCreators = new HashMap<>();

    public RuleFactory(Set<String> allShiftTypes) {
        this.allShiftTypes = allShiftTypes;
        setUpCreators();
    }

    private void setUpCreators() {
        holisticRuleCreators.put(RuleType.MaxAverageHoursPerWeek, ruleArgs -> {
            RuleParamArgs maxHours = getParamArgs(ruleArgs, "MaxHours");
            return new MaxAverageHoursPerWeekRule(Integer.parseInt(maxHours.getInput()));
        });

        softRuleCreators.put(RuleType.AverageHoursBalance, ruleArgs -> {
            RuleParamArgs weight = getParamArgs(ruleArgs, "Weight");
            return new AverageHoursBalanceSoftRule(Integer.parseInt(weight.getInput()));
        });

        softRuleCreators.put(RuleType.ShiftTypeBalance, ruleArgs -> {
            RuleParamArgs weight = getParamArgs(ruleArgs, "Weight");
            return new ShiftTypeBalanceSoftRule(Integer.parseInt(weight.getInput()), allShiftTypes);
        });

        softRuleCreators.put(RuleType.ShiftBlocks, ruleArgs -> {
            RuleParamArgs weight = getParamArgs(ruleArgs, "Weight");
            RuleParamArgs shiftName = getParamArgs(ruleArgs, "ShiftName");
            RuleParamArgs daysInBlock = getParamArgs(ruleArgs, "DaysInBlock");
            // todo this should be better than just comma separated
            String[] days = daysInBlock.getInput().toUpperCase().split(",");
            List<DayOfWeek> daysOfWeek = Arrays.stream(days).map(DayOfWeek::valueOf).collect(Collectors.toList());
            return new ShiftBlocksSoftRule(Integer.parseInt(weight.getInput()), new ShiftBlock(shiftName.getInput(), daysOfWeek));
        });

        ruleCreators.put(RuleType.MinHoursBetweenShifts, ruleArgs -> {
            RuleParamArgs minHours = getParamArgs(ruleArgs, "MinHours");
            return new MinHoursBetweenShiftsRule(Integer.parseInt(minHours.getInput()));
        });

        ruleCreators.put(RuleType.MaxConsecutiveShifts, ruleArgs -> {
            RuleParamArgs maxConsecutive = getParamArgs(ruleArgs, "MaxConsecutive");
            RuleParamArgs shiftName = getParamArgs(ruleArgs, "ShiftName");

            return new MaxConsecutiveShiftRule(shiftName.getInput(), Integer.parseInt(maxConsecutive.getInput()));
        });

        ruleCreators.put(RuleType.MaxHoursPerWeek, ruleArgs -> {
            RuleParamArgs maxHours = getParamArgs(ruleArgs, "MaxHours");
            return new MaxHoursPerWeekRule(Integer.parseInt(maxHours.getInput()));
        });

        ruleCreators.put(RuleType.NoMoreThanOneWeekendInARow, ruleArgs -> new NoMoreThanOneConsecutiveWeekendsRule());

        ruleCreators.put(RuleType.MinHoursBreakAfterConsecutiveShifts, ruleArgs -> {
            RuleParamArgs maxConsecutive = getParamArgs(ruleArgs, "MaxConsecutive");
            RuleParamArgs shiftName = getParamArgs(ruleArgs, "ShiftName");
            RuleParamArgs minHours = getParamArgs(ruleArgs, "MinHours");

            String[] consecutiveArray = maxConsecutive.getInput().split(",");
            int[] maxConsecutiveShifts = new int[consecutiveArray.length];
            List<Integer> collect = Arrays.stream(consecutiveArray).map(Integer::parseInt).collect(Collectors.toList());
            for (int i = 0; i < collect.size(); i++) {
                maxConsecutiveShifts[i] = collect.get(i);
            }

            return new MinHoursAfterConsecutiveShiftsRule(shiftName.getInput(), new IntegerMatcher(maxConsecutiveShifts), Integer.parseInt(minHours.getInput()));
        });
    }

    // todo should be in a map by the time it comes out the JSON
    private RuleParamArgs getParamArgs(RuleArgs ruleArgs, String paramName) {
        for (RuleParamArgs args : ruleArgs.getParams()) {
            if (paramName.equals(args.getName())) {
                return args;
            }
        }
        return null;
    }

    public void addRule(RotaEngine rotaEngine, RuleArgs ruleArgs) {
        RuleType ruleType = RuleType.valueOf(ruleArgs.getName());

        if (ruleType.isHolistic()) {
            HolisticRule holisticRule = createHolisticRule(ruleType, ruleArgs);
            rotaEngine.addHolisticRule(holisticRule);
        } else if (ruleType.isSoft()) {
            SoftRule softRule = createSoftRule(ruleType, ruleArgs);
            rotaEngine.addSoftRule(softRule);
        } else {
            Rule rule = createRule(ruleType, ruleArgs);
            rotaEngine.addRule(rule);
        }
    }

    private HolisticRule createHolisticRule(RuleType ruleType, RuleArgs ruleArgs) {
        return holisticRuleCreators.get(ruleType).create(ruleArgs);
    }

    private Rule createRule(RuleType ruleType, RuleArgs ruleArgs) {
        return ruleCreators.get(ruleType).create(ruleArgs);
    }

    private SoftRule createSoftRule(RuleType ruleType, RuleArgs ruleArgs) {
        return softRuleCreators.get(ruleType).create(ruleArgs);
    }
}
