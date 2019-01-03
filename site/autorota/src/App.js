import React, { Component } from 'react';
import './App.css';
import { DateContainer } from './components/DateContainer';
import { EmployeeListContainer } from './employees/EmployeeListContainer';
import { RuleListContainer } from './rules/RuleListContainer';

class App extends Component {
  state = {
    fromDate: '',
    toDate: ''
  };

  constructor(props) {
    super(props);

    this.handleFromDateChange = this.handleFromDateChange.bind(this);
    this.handleToDateChange = this.handleToDateChange.bind(this);
  }

  handleFromDateChange(newFromDate) {
    this.setState({ fromDate: newFromDate });
  }

  handleToDateChange(newToDate) {
    this.setState({ toDate: newToDate });
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <div>
            <h2>AutoRota</h2>
            <DateContainer onFromDateChanged={this.handleFromDateChange} onToDateChanged={this.handleToDateChange} />
            <br />
            {/* temp to show that this Component gets updated with the events raised by child Components */}
            <p>From date: {this.state.fromDate}, To date: {this.state.toDate}</p>
            <div className="Horizontal-container">
            <EmployeeListContainer />
            <RuleListContainer />
            </div>
          </div>
        </header>
      </div>
    );
  }
}

export default App;
