import React from 'react';

export class RuleRow extends React.Component {
    constructor(props) {
        super(props);

        this.selectedRuleType = '';

        this.handleDelete = this.handleDelete.bind(this);
        this.handleSelect = this.handleSelect.bind(this);
    }

    handleDelete() {
        this.props.onRuleDelete(this.props.ruleId, this.selectedRuleType);
    }

    handleSelect(e) {
        const oldRuleType = this.selectedRuleType;
        this.selectedRuleType = e.target.value;
        this.props.onRuleTypeSelected(oldRuleType, e.target.value);
    }

    render() {
        const possibleRules = this.props.possibleRules.slice(); // take copy of array to not change props
        if (this.selectedRuleType) {
            possibleRules.push(this.selectedRuleType);
        }
        const options = possibleRules.map((x) =>
            <option value={x} key={x.toString()}>{x}</option>);
        return (<div className="Horizontal-container">
            <button className="Padded" onClick={this.handleDelete}>delete</button>
            <form>Type:
                <select value={this.selectedRuleType} onChange={this.handleSelect}>
                    {options}
                </select></form>
        </div>);
    }
}