import React from 'react';
import { Field, reduxForm } from 'redux-form';
import validateRotaForm from './validateRotaForm';

const renderError = ({ error, touched }) => {
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

const renderInput = ({ input, label, meta }) => {
    const className = `field ${meta.error && meta.touched ? 'error' : ''}`;
    return (
        <div className={className}>
            <label>{label}</label>
            <input {...input} autoComplete="off" type="date" />
            {renderError(meta)}
        </div>
    );
}

const DateFormPage = (props) => {
    return (
        <form onSubmit={props.handleSubmit}
            className="ui form error">
            <div className="two fields"
                style={{ "paddingRight": "40px", "paddingLeft": "40px" }}>
                <Field
                    name="startDate"
                    component={renderInput}
                    label="Start Date"
                />
                <Field
                    name="endDate"
                    component={renderInput}
                    label="End Date"
                    type="date" />

            </div>
            {props.backButton}
            {props.submitButton}
        </form>
    );
}

export default reduxForm({
    form: 'rotaWizard',
    destroyOnUnmount: false,
    forceUnregisterOnUnmount: true,
    validateRotaForm
})(DateFormPage);