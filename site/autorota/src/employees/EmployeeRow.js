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
        return (<div className="Horizontal-container">
            <button className="Padded" onClick={this.handleDelete}>delete</button> 
            <form >Name: <input type="text"/></form>
        </div>);
    }
}