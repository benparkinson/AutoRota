package com.parkinsonhardy.autorota.controllers.parsers;

import com.parkinsonhardy.autorota.engine.RotaEngine;
import com.parkinsonhardy.autorota.engine.ShiftBlock;
import com.parkinsonhardy.autorota.exceptions.RuleCreationException;
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
            String maxHours = getParameter(ruleArgs, "MaxHours", RuleType.MaxAverageHoursPerWeek);
            return new MaxAverageHoursPerWeekRule(Integer.parseInt(maxHours));
        });

        softRuleCreators.put(RuleType.AverageHoursBalance, ruleArgs -> {
            String weight = getParameter(ruleArgs, "Weight", RuleType.AverageHoursBalance);
            return new AverageHoursBalanceSoftRule(Integer.parseInt(weight));
        });

        softRuleCreators.put(RuleType.ShiftTypeBalance, ruleArgs -> {
            String input = getParameter(ruleArgs, "Weight", RuleType.ShiftTypeBalance);
            return new ShiftTypeBalanceSoftRule(Integer.parseInt(input), allShiftTypes);
        });

        softRuleCreators.put(RuleType.ShiftBlocks, ruleArgs -> {
            String weight = getParameter(ruleArgs, "Weight", RuleType.ShiftBlocks);
            RuleParamArgs shiftName = getParamArgs(ruleArgs, "ShiftName");
            RuleParamArgs daysInBlock = getParamArgs(ruleArgs, "DaysInBlock");
            String fromDay = daysInBlock.getFrom().toUpperCase();
            String toDay = daysInBlock.getTo().toUpperCase();
            DayOfWeek from = DayOfWeek.valueOf(fromDay);
            DayOfWeek to = DayOfWeek.valueOf(toDay);
            List<DayOfWeek> daysOfWeek = new ArrayList<>();
            DayOfWeek i = from;
            while (i != to.plus(1)) {
                daysOfWeek.add(i);
                i = i.plus(1);
            }

            // todo rule could be a different class if forced (since it's no longer a soft rule...), need to consider options
            RuleParamArgs force = getParamArgs(ruleArgs, "Force");
            boolean mandatory = Boolean.valueOf(force.getInput());
            int weightValue;
            // bit of a hack to set weight to 0 in this case...
            if (mandatory) {
                weightValue = 0;
            } else {
                weightValue = Integer.parseInt(weight);
            }
            return new ShiftBlocksSoftRule(weightValue,
                    new ShiftBlock(shiftName.getInput(), daysOfWeek), mandatory);
        });

        softRuleCreators.put(RuleType.AvoidSingleShifts, ruleArgs -> {
            String weight = getParameter(ruleArgs, "Weight", RuleType.AvoidSingleShifts);
            return new AvoidSingleShiftsSoftRule(Integer.parseInt(weight));
        });

        ruleCreators.put(RuleType.MinHoursBetweenShifts, ruleArgs -> {
            String minHours = getParameter(ruleArgs, "MinHours", RuleType.MinHoursBetweenShifts);
            return new MinHoursBetweenShiftsRule(Integer.parseInt(minHours));
        });

        ruleCreators.put(RuleType.MaxConsecutiveShifts, ruleArgs -> {
            String maxConsecutive = getParameter(ruleArgs, "MaxConsecutive", RuleType.MaxConsecutiveShifts);
            String shiftName = getParameter(ruleArgs, "ShiftName", RuleType.MaxConsecutiveShifts);

            return new MaxConsecutiveShiftRule(shiftName, Integer.parseInt(maxConsecutive));
        });

        ruleCreators.put(RuleType.MaxHoursPerWeek, ruleArgs -> {
            String maxHours = getParameter(ruleArgs, "MaxHours", RuleType.MaxHoursPerWeek);
            return new MaxHoursPerWeekRule(Integer.parseInt(maxHours));
        });

        ruleCreators.put(RuleType.NoMoreThanOneWeekendInARow, ruleArgs -> new NoMoreThanOneConsecutiveWeekendsRule());

        ruleCreators.put(RuleType.MinHoursBreakAfterConsecutiveShifts, ruleArgs -> {
            String maxConsecutive = getParameter(ruleArgs, "MaxConsecutive", RuleType.MinHoursBreakAfterConsecutiveShifts);
            String shiftName = getParameter(ruleArgs, "ShiftName", RuleType.MinHoursBreakAfterConsecutiveShifts);
            String minHours = getParameter(ruleArgs, "MinHours", RuleType.MinHoursBreakAfterConsecutiveShifts);

            // todo obviously should be more clear on GUI that multiple values are necessary
            String[] consecutiveArray = maxConsecutive.split(",");
            int[] maxConsecutiveShifts = new int[consecutiveArray.length];
            List<Integer> collect = Arrays.stream(consecutiveArray).map(Integer::parseInt).collect(Collectors.toList());
            for (int i = 0; i < collect.size(); i++) {
                maxConsecutiveShifts[i] = collect.get(i);
            }

            return new MinHoursAfterConsecutiveShiftsRule(shiftName, new IntegerMatcher(maxConsecutiveShifts), Integer.parseInt(minHours));
        });
    }

    private String getParameter(RuleArgs ruleArgs, String paramName, RuleType ruleType) throws RuleCreationException {
        RuleParamArgs weight = getParamArgs(ruleArgs, paramName);
        String input = weight.getInput();
        if (input == null) {
            throw new RuleCreationException(String.format("No input provided for parameter: [%s] for rule: [%s]", paramName, ruleType.name()));
        }
        return input;
    }

    // todo should be converted to a map by the time it comes out the JSON
    private RuleParamArgs getParamArgs(RuleArgs ruleArgs, String paramName) throws RuleCreationException {
        for (RuleParamArgs args : ruleArgs.getParams()) {
            if (paramName.equals(args.getName())) {
                return args;
            }
        }

        throw new RuleCreationException(String.format("Could not find expected parameter: [%s] in rule arg: [%s]",
                paramName, ruleArgs.toString()));
    }

    public void addRule(RotaEngine rotaEngine, RuleArgs ruleArgs) throws RuleCreationException {
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

    private HolisticRule createHolisticRule(RuleType ruleType, RuleArgs ruleArgs) throws RuleCreationException {
        return holisticRuleCreators.get(ruleType).create(ruleArgs);
    }

    private Rule createRule(RuleType ruleType, RuleArgs ruleArgs) throws RuleCreationException {
        return ruleCreators.get(ruleType).create(ruleArgs);
    }

    private SoftRule createSoftRule(RuleType ruleType, RuleArgs ruleArgs) throws RuleCreationException {
        return softRuleCreators.get(ruleType).create(ruleArgs);
    }
}
