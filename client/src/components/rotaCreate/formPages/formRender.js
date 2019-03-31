import React from 'react'

export const renderError = ({ error, touched }) => {
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

// for some strange reason checkbox labels have to be after the input or 
// else it won't toggle on click, probably some SemanticUI quirk.
const renderToggle = (input, label, meta) => {
    const className = "field ui toggle checkbox";
    return (
        <div className={className}>
            <input {...input} type="checkbox" />
            <label>{label}</label>
            {renderError(meta)}
        </div>
    );
}

export const renderInput = ({ input, label, meta, type }) => {
    if (type === "checkbox") {
        return renderToggle(input, label, meta);
    }
    const className = `field ${meta.error && meta.touched ? 'error' : ''}`;
    return (
        <div className={className}>
            <label>{label}</label>
            <input {...input} type={type} autoComplete="off" />
            {renderError(meta)}
        </div>
    );
}