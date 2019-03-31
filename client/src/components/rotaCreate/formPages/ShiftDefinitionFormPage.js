import React from 'react';
import { Field, FieldArray, reduxForm } from 'redux-form';
import converter from 'number-to-words';
import validateRotaForm from './validateRotaForm';
import { renderInput } from './formRender';

class ShiftDefinitionFormPage extends React.Component {

    renderDayRequirements = (shiftDefinition) => {
        const days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];

        const renderedDays = days.map(day => {
            return (
                <Field name={`${shiftDefinition}.dayRequirements.${day.toLowerCase()}`} component={renderInput}
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
        return (
            <div>

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
                                component={renderInput} type="text" label="Shift Name" />
                            <Field name={`${shiftDefinition}.shiftStart`}
                                component={renderInput} type="time" label="Start Time" />
                            <Field name={`${shiftDefinition}.shiftEnd`}
                                component={renderInput} type="time" label="End Time" />
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
            </div>
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
    validate: validateRotaForm
})(ShiftDefinitionFormPage);