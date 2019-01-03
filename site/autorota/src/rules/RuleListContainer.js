import React from 'react';
import { RuleRow } from './RuleRow';

export class RuleListContainer extends React.Component {
    state = {
        rules: [],
        possibleRules: ['', 'MinHoursBetweenShifts', 'MaxAverageHoursPerWeek',
            'MaxConsecutiveShifts', 'MaxHoursPerWeek', 'NoMoreThanOneWeekendInARow']
    }

    constructor(props) {
        super(props);

        this.nextRuleId = 0;

        this.handleAddRule = this.handleAddRule.bind(this);
        this.handleDeleteRule = this.handleDeleteRule.bind(this);
        this.handleRuleTypeSelected = this.handleRuleTypeSelected.bind(this);
    }

    createNewRule() {
        return this.nextRuleId++;
    }

    handleAddRule() {
        const rule = this.createNewRule();
        this.setState((prevState) => ({
            rules: prevState.rules.concat([rule])
        }));
    }

    handleDeleteRule(ruleId, ruleType) {
        this.setState((prevState) => ({
            rules: prevState.rules.filter(function (rule) {
                return rule !== ruleId
            }
            )
        }));

        if (ruleType) {
            this.setState((prevState) => ({
                possibleRules: prevState.possibleRules.concat([ruleType])
            }));
        }
    }

    handleRuleTypeSelected(from, to) {
        if (from) {
            this.setState((prevState) => ({
                possibleRules: prevState.possibleRules.concat([from])
            }));
        }

        if (to) {
            this.setState((prevState) => ({
                possibleRules: prevState.possibleRules.filter(function (ruleType) {
                    return ruleType !== to
                }
                )
            }));
        }
    }

    render() {
        const ruleItems = this.state.rules.map((x) =>
            <RuleRow onRuleDelete={this.handleDeleteRule}
                possibleRules={this.state.possibleRules}
                onRuleTypeSelected={this.handleRuleTypeSelected}
                ruleId={x} key={x.toString()}></RuleRow>
        );
        return (
            <div>
                <h4>Rules</h4>
                {ruleItems}
                <button onClick={this.handleAddRule}>add</button>
            </div>);
    }
}