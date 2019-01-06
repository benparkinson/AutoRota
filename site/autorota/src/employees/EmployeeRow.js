import React from 'react';

export class EmployeeRow extends React.Component {
    constructor(props) {
        super(props);

        this.handleDelete = this.handleDelete.bind(this);
    }

    handleDelete() {
        this.props.onEmployeeDelete(this.props.employeeId);
    }

    render() {
        return (
            <div className="ui input">
                <button className="ui icon button" onClick={this.handleDelete}>
                    <i className="trash icon"></i>
                </button>
                <form className="ui form">Name: <input type="text" /></form>
            </div>
        );
    }
}