import React, { Component } from 'react';
import './App.css';
import { DateContainer } from './components/DateContainer';
import { EmployeeListContainer } from './employees/EmployeeListContainer';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      fromDate: '',
      toDate: '',
      employees: []
    };

    this.handleFromDateChange = this.handleFromDateChange.bind(this);
    this.handleToDateChange = this.handleToDateChange.bind(this);
    this.handleAddNewEmployee = this.handleAddNewEmployee.bind(this);
  }

  handleFromDateChange(newFromDate) {
    this.setState({ fromDate: newFromDate });
  }

  handleToDateChange(newToDate) {
    this.setState({ toDate: newToDate });
  }

  createNewEmployee() {
    return this.state.employees.length;
  }

  handleAddNewEmployee() {
    const employee = this.createNewEmployee();
    this.setState((prevState) => ({
      employees: prevState.employees.concat([employee])
    }));
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <div>
            <h2>AutoRota</h2>
            <DateContainer onFromDateChanged={this.handleFromDateChange} onToDateChanged={this.handleToDateChange} />
            <br/>
            {/* temp to show that this Component gets updated with the events raised by child Components */}
            <p>From date: {this.state.fromDate}, To date: {this.state.toDate}</p>
            <EmployeeListContainer onAddNewEmployee={this.handleAddNewEmployee} employees={this.state.employees} />
          </div>
        </header>
      </div>
    );
  }
}

export default App;
