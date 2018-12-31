import React from 'react';

export class EmployeeRow extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <div>{this.props.employeeId}: Ready to work</div>;
    }
}