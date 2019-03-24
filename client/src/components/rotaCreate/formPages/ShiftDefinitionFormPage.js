import React from 'react';
import { Field, FieldArray, reduxForm } from 'redux-form';
import converter from 'number-to-words';
import validateRotaForm from './validateRotaForm';

class ShiftDefinitionFormPage extends React.Component {

    renderError = ({ error, touched }) => {
        if (touched && error) {
            return (
                <div className="ui error message">
                    <div className="header">
                        {error}
                    </div>
                </div>
            );
        }
    }

    renderInput = ({ input, label, meta, type }) => {
        const className = `field ${meta.error && meta.touched ? 'error' : ''}`;
        return (
            <div className={className}>
                <label>{label}</label>
                <input {...input} type={type} autoComplete="off" />
                {this.renderError(meta)}
            </div>
        );
    }

    renderDayRequirements = (shiftDefinition) => {
        const days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];

        const renderedDays = days.map(day => {
            return (
                <Field name={`${shiftDefinition}.dayRequirements.${day.toLowerCase()}`} component={this.renderInput}
                    type="number" label={day} key={day} />
            );
        })

        return (
            <div className={`${converter.toWords(days.length)} fields`}>
                {renderedDays}
            </div>
        );
    }

    renderShiftDefinitions = ({ fields }) => {
        // todo don't hardcode the padding, this is only hinging on the browser defaulting a ul style to pad 40px
        // should ideally be relative and probably set the padding on each side to be the same, maybe pass this in so
        // all form pages are the same?
        return (
            <ul style={{ "paddingRight": "40px" }}>

                <div className="ui field"></div>

                {fields.map((shiftDefinition, index) => (
                    <div key={index} className="ui segment">
                        <button
                            className="ui icon button right floated field"
                            type="button"
                            title="Remove Shift Definition"
                            onClick={() => fields.remove(index)}
                        >
                            <i className="center aligned trash icon"></i>
                        </button>
                        <strong className="ui header">Shift #{index + 1}</strong>
                        <div className="three fields">
                            <Field name={`${shiftDefinition}.shiftName`}
                                component={this.renderInput} type="text" label="Shift Name" />
                            <Field name={`${shiftDefinition}.shiftStart`}
                                component={this.renderInput} type="time" label="Start Time" />
                            <Field name={`${shiftDefinition}.shiftEnd`}
                                component={this.renderInput} type="time" label="End Time" />
                        </div>
                        <strong># of Doctors Required:
                        </strong>
                        {this.renderDayRequirements(shiftDefinition)}
                    </div>
                ))
                }

                <div className="ui center aligned container">
                    <button className="ui field secondary button" type="button" onClick={() => fields.push({})}>
                        Add Shift Definition
                    </button>
                </div>
            </ul>
        );
    }

    render() {
        return (
            <form onSubmit={this.props.handleSubmit} className="ui form error">
                <FieldArray name="shiftDefinitions" component={this.renderShiftDefinitions} />
                {this.props.backButton}
                {this.props.submitButton}
            </form>
        );
    }
}

export default reduxForm({
    form: 'rotaWizard',
    destroyOnUnmount: false,
    forceUnregisterOnUnmount: true,
    validateRotaForm
})(ShiftDefinitionFormPage);