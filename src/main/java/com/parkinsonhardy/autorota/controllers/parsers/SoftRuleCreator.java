package com.parkinsonhardy.autorota.controllers.parsers;

import com.parkinsonhardy.autorota.model.RuleArgs;
import com.parkinsonhardy.autorota.rules.SoftRule;

public interface SoftRuleCreator {
    SoftRule create(RuleArgs ruleArgs);
}
