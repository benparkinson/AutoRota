import React from 'react';
import { RuleRow } from './RuleRow';
import FluidCard from '../components/FluidCard';

export class RuleListContainer extends React.Component {
    state = {
        rules: [],
        // this will come from server
        possibleRules: [{ name: '', params: [] },
        { name: 'MinHoursBetweenShifts', params: [{ name: 'MinHours', type: 'number' }] },
        { name: 'MaxAverageHoursPerWeek', params: [{ name: 'MaxHours', type: 'number' }] },
        {
            name: 'MaxConsecutiveShifts', params: [{ name: 'ShiftName', type: 'string' },
            { name: 'MaxConsecutive', type: 'number' }]
        },
        { name: 'MaxHoursPerWeek', params: [{ name: 'MaxHours', type: 'number' }] },
        { name: 'NoMoreThanOneWeekendInARow', params: [{ name: '', type: 'boolean' }] },
        {
            name: 'MinHoursBreakAfterConsecutiveShifts', params: [{ name: 'ShiftName', type: 'string' },
            { name: 'MaxConsecutive', type: 'string' }, { name: 'MinHours', type: 'number' }]
        }
        ]
    }

    constructor(props) {
        super(props);


        this.nextRuleId = 0;
        this.allRules = this.state.possibleRules.slice();

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
            const toAdd = this.allRules.find(x => x.name === ruleType);
            this.setState((prevState) => ({
                possibleRules: prevState.possibleRules.concat([toAdd])
            }));
        }
    }

    handleRuleTypeSelected(from, to) {
        if (from) {
            const toAdd = this.allRules.find(x => x.name === from);
            this.setState((prevState) => ({
                possibleRules: prevState.possibleRules.concat([toAdd])
            }));
        }

        if (to) {
            this.setState((prevState) => ({
                possibleRules: prevState.possibleRules.filter(function (ruleType) {
                    return ruleType.name !== to
                }
                )
            }));
        }
    }

    render() {
        const ruleItems = this.state.rules.map((x) =>
            <FluidCard key={x}>
                <RuleRow onRuleDelete={this.handleDeleteRule}
                    possibleRules={this.state.possibleRules}
                    onRuleTypeSelected={this.handleRuleTypeSelected}
                    ruleId={x} ></RuleRow>
            </FluidCard>
        );
        return (
            <div>
                <h4>Rules</h4>
                {ruleItems}
                <button className="ui icon button" onClick={this.handleAddRule}>
                    <i className="add icon"></i>
                </button>
            </div>
        );
    }
}