import React from 'react';
import { EmployeeRow } from './EmployeeRow';

export class EmployeeListContainer extends React.Component {
    state = {
        employees: []
    }

    constructor(props) {
        super(props);

        this.nextEmployeeId = 0;

        this.handleAddEmployee = this.handleAddEmployee.bind(this);
        this.handleDeleteEmployee = this.handleDeleteEmployee.bind(this);
    }

    createNewEmployee() {
        return this.nextEmployeeId++;
    }

    handleAddEmployee() {
        const employee = this.createNewEmployee();
        this.setState((prevState) => ({
            employees: prevState.employees.concat([employee])
        }));
    }

    handleDeleteEmployee(employeeId) {
        this.setState((prevState) => ({
            employees: prevState.employees.filter(function (employee) {
                return employee !== employeeId
            }
            )
        }));
    }

    render() {
        const employeeItems = this.state.employees.map((x) =>
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