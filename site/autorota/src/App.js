import React, { Component } from 'react';
import './App.css';
import { DateContainer } from './components/DateContainer';
import { EmployeeListContainer } from './employees/EmployeeListContainer';
import { RuleListContainer } from './rules/RuleListContainer';
import { ShiftDefinitionContainer } from './shiftdefs/ShiftDefinitionContainer';

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
      <header>
        <h2 className="ui header">AutoRota</h2>
        <div>
          <DateContainer onFromDateChanged={this.handleFromDateChange} onToDateChanged={this.handleToDateChange} />
          <br />
          <div className="ui grid">
            <div className="ten column row">
              <ShiftDefinitionContainer />
            </div>
            <div className="sixteen column row">
              <div className="column"></div>
              <div className="seven wide column">
                <EmployeeListContainer />
              </div>
              <div className="seven wide column">
                <RuleListContainer />
              </div>
              <div className="column"></div>
            </div>
          </div>
        </div>
      </header>
    );
  }
}

export default App;
