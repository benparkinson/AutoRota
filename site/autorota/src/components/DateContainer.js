import React from 'react';
import { DatePicker } from './DatePicker';

export class DateContainer extends React.Component {
    constructor(props) {
        super(props);

        this.handleFromDateChanged = this.handleFromDateChanged.bind(this);
        this.handleToDateChanged = this.handleToDateChanged.bind(this);
    }

    handleFromDateChanged(fromDate) {
        this.props.onFromDateChanged(fromDate);
    }

    handleToDateChanged(toDate) {
        this.props.onToDateChanged(toDate);
    }

    render() {
        return (
            <div className="Date-container">
                <DatePicker label="From:" onDateChange={this.handleFromDateChanged} /> 
                
                <DatePicker label="To:" onDateChange={this.handleToDateChanged} />
            </div>
        );
    }
}