const validateRotaForm = (values) => {
    const validation = {};

    const firstValues = {};

    if (!values.hardRules || values.hardRules.length === 0)
        validation.hardRules = "Define at least one hard rule!";
    else {
        const hardRuleErrors = [];
        values.hardRules.forEach((rule, ruleIndex) => {
            var errorsForRule = '';
            if (!rule.name) {
                errorsForRule = "Required!"
            } else {
                const { name, unique } = rule;
                if (!name) {
                    errorsForRule = "Required!"
                } else if (firstValues[name] === undefined) {
                    firstValues[name] = 1;
                } else if (unique) {
                    errorsForRule = "Rule already defined! Only one rule per type allowed";
                }
            }
            hardRuleErrors[ruleIndex] = errorsForRule;
        })
        if (hardRuleErrors.length) {
            validation.hardRules = hardRuleErrors;
        }
    }

    if (!values.name) {
        validation.name = "Please enter a rota name";
    }

    return validation;
}

export default validateRotaForm;