package com.parkinsonhardy.autorota.controllers.parsers;

import com.parkinsonhardy.autorota.model.RuleArgs;
import com.parkinsonhardy.autorota.rules.HolisticRule;

public interface HolisticRuleCreator {
    HolisticRule create(RuleArgs ruleArgs);
}
