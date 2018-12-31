import React from 'react';
import { EmployeeRow } from './EmployeeRow';

export class EmployeeListContainer extends React.Component {
    constructor(props) {
        super(props);

        this.handleAddEmployee = this.handleAddEmployee.bind(this);
    }

    handleAddEmployee() {
        this.props.onAddNewEmployee();
    }

    render() {
        const employeeItems = this.props.employees.map((x) =>
            <EmployeeRow employeeId={x} key={x.toString()}></EmployeeRow>
        );
        return (
            <div>
                <h3>Employees</h3>
                {employeeItems}
                <button onClick={this.handleAddEmployee}>Add</button>
            </div>);
    }
}