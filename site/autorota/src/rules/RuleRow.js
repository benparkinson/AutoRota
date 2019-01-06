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
        const ruleNames = this.props.possibleRules.map(x => x.name);
        if (this.selectedRuleType) {
            ruleNames.push(this.selectedRuleType);
        }
        const options = ruleNames.map((x) =>
            <option value={x}
                key={x}>
                {x}
            </option>
        );
        return (
            <div className="ui input">
                <button className="ui icon button" onClick={this.handleDelete}>
                    <i className="trash icon"></i>
                </button>

                <form className="ui form">Type:
                    <select className="ui dropdown"
                          style={{ maxWidth: '100%' }}
                            value={this.selectedRuleType} onChange={this.handleSelect}>
                           {options}
                    </select>
                </form>
            </div>
        );
    }
}