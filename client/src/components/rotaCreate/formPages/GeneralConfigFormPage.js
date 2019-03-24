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

const renderInput = ({ input, label, meta, type }) => {
    const className = `field ${meta.error && meta.touched ? 'error' : ''}`;
    return (
        <div className={className}>
            <label>{label}</label>
            <input {...input} autoComplete="off" type={type} />
            {renderError(meta)}
        </div>
    );
}

const GeneralConfigFormPage = (props) => {
    return (
        <form onSubmit={props.handleSubmit}
            className="ui form error">
            <div className="ui field"></div>
            <div className="ui field">
                <div className="ui segment">
                    <strong className="ui header">Dates</strong>
                    <div className="two fields">
                        <Field
                            name="startDate"
                            component={renderInput}
                            label="Start Date"
                            type="date"
                        />
                        <Field
                            name="endDate"
                            component={renderInput}
                            label="End Date"
                            type="date" />

                    </div>
                </div>
                <div className="ui segment">
                    <strong className="ui header">Extra Config</strong>
                    <div className="two fields">
                        <Field
                            name="name"
                            component={renderInput}
                            label="Rota Name (just to help you remember)"
                            type="text"
                        />
                        <Field
                            name="timeout"
                            component={renderInput}
                            label="Timeout (seconds)"
                            type="number"
                        />
                    </div>
                </div>
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
})(GeneralConfigFormPage);