import React from 'react';
import { EmployeeRow } from './EmployeeRow';

export class EmployeeListContainer extends React.Component {
    constructor(props) {
        super(props);

        this.handleAddEmployee = this.handleAddEmployee.bind(this);
        this.handleDeleteEmployee = this.handleDeleteEmployee.bind(this);
    }

    handleAddEmployee() {
        this.props.onAddNewEmployee();
    }

    handleDeleteEmployee(employeeId) {
        this.props.onDeleteEmployee(employeeId);
    }

    render() {
        const employeeItems = this.props.employees.map((x) =>
            <EmployeeRow onEmployeeDelete={this.handleDeleteEmployee} employeeId={x} key={x.toString()}></EmployeeRow>
        );
        return (
            <div>
                <h4>Employees</h4>
                {employeeItems}
                <button onClick={this.handleAddEmployee}>add</button>
            </div>);
    }
}