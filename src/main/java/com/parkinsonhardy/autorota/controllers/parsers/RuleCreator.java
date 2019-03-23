package com.parkinsonhardy.autorota.controllers.parsers;

import com.parkinsonhardy.autorota.model.RuleArgs;
import com.parkinsonhardy.autorota.rules.Rule;

public interface RuleCreator {
    Rule create(RuleArgs ruleArgs);
}
