import React from 'react'

export const renderError = ({ error, touched, active }) => {
    if (touched && error && !active) {
        return (
            <div className="ui error message">
                <div className="header">
                    {error}
                </div>
            </div>
        );
    }
}

// for some strange reason checkbox labels have to be after the input or 
// else it won't toggle on click, probably some SemanticUI quirk.   
const renderToggle = (input, label, meta) => {
    const className = "ui field toggle checkbox";
    return (
        <div className="ui compact basic segment">
            <div className="field">
                <div className={className}>
                    <input {...input} type="checkbox" />
                    <label>{label}</label>
                    {renderError(meta)}
                </div>
            </div>
        </div>
    );
}

export const renderInput = ({ input, label, meta, type }) => {
    if (type === "checkbox") {
        return renderToggle(input, label, meta);
    }
    const className = `field ${meta.error && meta.touched && !meta.active ? 'error' : ''}`;
    return (
        <div className={className}>
            <label>{label}</label>
            <input {...input} type={type} autoComplete="off" />
            {renderError(meta)}
        </div>
    );
}