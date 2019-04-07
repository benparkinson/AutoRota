const validateRotaForm = (values) => {
    const validation = {};

    validation.hardRules = validateRules(values.hardRules);

    validation.softRules = validateRules(values.softRules);

    validation.shiftDefinitions = validateShiftDefinitions(values.shiftDefinitions);

    ["name", "startDate", "endDate", "timeout"].forEach(field => {
        if (!values[field]) {
            validation[field] = "Required";
        }
    });

    validation.doctors = [];
    if (values.doctors) {
        values.doctors.forEach((doctor, index) => {
            var error = {};
            if (!doctor.name) {
                error.name = "Required";
                validation.doctors[index] = error;
            }
        })
    }

    return validation;
}

function validateRules(values) {
    let validation;
    const firstValues = {};
    if (values && values.length) {
        const ruleErrors = [];
        values.forEach((rule, ruleIndex) => {
            const errorsForRule = {};
            if (!rule.name) {
                errorsForRule.name = "Required!";
                ruleErrors[ruleIndex] = errorsForRule;
                return;
            }

            const { name, unique } = rule;
            if (firstValues[name] === undefined) {
                firstValues[name] = 1;
            }
            else if (unique) {
                errorsForRule.name = "Rule already defined! Only one rule per type allowed";
                ruleErrors[ruleIndex] = errorsForRule;
            }
            if (!errorsForRule.name) {
                errorsForRule.params = [];
                if (!rule.params || rule.params.length === 0) {
                    return;
                }
                rule.params.forEach((param, paramIndex) => {
                    let error = {};
                    if (param.type === "checkbox") {
                        return;
                    }
                    if (param.type === "day_range") {
                        if (!param.from) {
                            error.from = "Required";
                            errorsForRule.params[paramIndex] = error;
                            ruleErrors[ruleIndex] = errorsForRule;
                        }
                        if (!param.to) {
                            error.to = "Required";
                            errorsForRule.params[paramIndex] = error;
                            ruleErrors[ruleIndex] = errorsForRule;
                        }
                    } else {
                        if (!param.input) {
                            error.input = "Required";
                            errorsForRule.params[paramIndex] = error;
                            ruleErrors[ruleIndex] = errorsForRule;
                        }
                    }
                });
            }
        });
        if (ruleErrors.length) {
            validation = ruleErrors;
        }
    }
    return validation;
}

function validateShiftDefinitions(values) {
    let validation;

    if (values && values.length) {
        const shiftErrors = [];
        values.forEach((shiftDef, shiftIndex) => {
            const errorsForShift = {};
            ["shiftName", "shiftStart", "shiftEnd"].forEach((field) => {
                if (!shiftDef[field]) {
                    errorsForShift[field] = "Required";
                    shiftErrors[shiftIndex] = errorsForShift;
                }
            });

            if (shiftDef.dayRequirements) {
                const dayRequirementErrors = {};
                ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"].forEach((day) => {
                    if (!shiftDef.dayRequirements[day]) {
                        dayRequirementErrors[day] = "Required";
                        errorsForShift.dayRequirements = dayRequirementErrors;
                        shiftErrors[shiftIndex] = errorsForShift;
                    }
                });
            }
        });

        if (shiftErrors.length) {
            validation = shiftErrors;
        }
    }
    return validation;
}

export default validateRotaForm;