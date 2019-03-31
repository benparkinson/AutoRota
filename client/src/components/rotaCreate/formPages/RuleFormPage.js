import React from 'react';
import { Field, FieldArray } from 'redux-form';
import { renderInput, renderError } from './formRender';

class RuleFormPage extends React.Component {

    renderShiftOptions = ({ input, label, meta }) => {
        if (!this.props.shiftTypes)
            return null;

        const optionsRendered = this.props.shiftTypes.map((x) =>
            <option value={x}
                key={x}>
                {x}
            </option>
        );

        const className = `field ${meta.error && meta.touched ? 'error' : ''}`;
        return (
            <div className={className}>
                <label>{label}</label>
                <select className="ui dropdown">
                    {optionsRendered}
                </select>
                {renderError(meta)}
            </div>
        );
    }

    renderRuleParams(name, rule) {
        if (!rule || !rule.params || rule.params.length === 0) {
            return null;
        }

        const createField = (param, index) => {
            let component;
            if (param.type === "shift") {
                component = this.renderShiftOptions;
            } else {
                component = renderInput
            }

            return (
                <Field name={`${name}.params[${index + i}].input`} label={param.name}
                    key={param.name}
                    component={component}
                    type={param.type} />
            );
        }

        const renderedParams = [];
        for (var i = 0; i < rule.params.length; i += 2) {
            const subArray = rule.params.slice(i, i + 2);
            const subParams = subArray.map(createField);
            renderedParams.push((
                <div className="two fields" key={i}>
                    {subParams}
                </div>
            ));
        }

        return (
            <div className="ui segment">
                {renderedParams}
            </div>
        );
    }

    renderRuleSelect = ({ input, label, meta, options, ...rest }) => {
        if (!this.props.possibleRules)
            return null;

        const optionsRendered = options.map((x) =>
            <option value={JSON.stringify(x)}
                key={x.name}>
                {x.niceName}
            </option>
        );

        const parseValue = (event) => {
            return JSON.parse(event.target.value);
        };

        const val = JSON.stringify(options.find(option => option.name === input.value.name))

        const className = `field ${meta.error && meta.touched ? 'error' : ''}`;
        return (
            <div className={className}>
                <label>{label}</label>
                <select className="ui dropdown"
                    onChange={event => input.onChange(parseValue(event))}
                    onBlur={event => input.onBlur(parseValue(event))}
                    value={val}
                    {...rest}
                >
                    {optionsRendered}
                </select>
                {this.renderRuleParams(input.name, input.value)}
                {renderError(meta)}
            </div>
        );
    }

    renderRules = ({ fields, options }) => {
        return (
            <div>
                <div className="ui field"></div>

                {fields.map((rule, index) => (
                    <div key={index} className="ui segment">
                        <button
                            className="ui icon button right floated field"
                            type="button"
                            title="Remove Rule"
                            onClick={() => fields.remove(index)}
                        >
                            <i className="center aligned trash icon"></i>
                        </button>
                        <strong className="ui header">Rule #{index + 1}</strong>
                        <Field name={`${rule}`}
                            component={this.renderRuleSelect} options={options} label="Rule Type" />
                    </div>
                ))
                }

                <div className="ui center aligned container">
                    <button className="ui field secondary button" type="button" onClick={() => fields.push({})}>
                        Add Rule
                    </button>
                </div>
            </div>
        );
    }

    render() {
        return (
            <FieldArray name={this.props.name} component={this.renderRules} options={this.props.possibleRules} />
        );
    }
}

export default RuleFormPage;