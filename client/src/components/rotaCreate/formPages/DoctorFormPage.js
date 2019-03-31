import React from 'react';
import { Field, FieldArray, reduxForm } from 'redux-form';
import validateRotaForm from './validateRotaForm';
import { renderInput } from './formRender';

const renderDoctors = ({ fields }) => {
    return (
        <div>
            <div className="ui field"></div>

            {fields.map((doctor, index) => (
                <div key={index} className="ui segment">
                    <button
                        className="ui icon button right floated field"
                        type="button"
                        title="Remove Doctor"
                        onClick={() => fields.remove(index)}
                    >
                        <i className="center aligned trash icon"></i>
                    </button>
                    <strong className="ui header">Doctor #{index + 1}</strong>
                    <Field name={`${doctor}.name`} label="Name"
                        component={renderInput} type="text" />
                </div>
            ))
            }

            <div className="ui center aligned container">
                <button className="ui field secondary button" type="button" onClick={() => fields.push({})}>
                    Add Doctor
                </button>
            </div>
        </div>
    );
}

const DoctorFormPage = (props) => {
    return (
        <form onSubmit={props.handleSubmit} className="ui form error">
            <FieldArray name="doctors" component={renderDoctors} />
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
})(DoctorFormPage);