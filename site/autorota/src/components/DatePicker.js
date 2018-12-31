import React from 'react';

export class DatePicker extends React.Component {
    constructor(props) {
        super(props);

        this.handleDateChange = this.handleDateChange.bind(this);
    }

    handleDateChange(e) {
        this.props.onDateChange(e.target.value);
    }

    render() {
        return (<div className="Padded">
            <form >
            {this.props.label} {' '}
                <input type="date" onChange={this.handleDateChange}/>
            </form>
        </div>);
    }
}