import React from 'react';

export class ShiftDefinitionRow extends React.Component {
    constructor(props) {
        super(props);

        this.handleDelete = this.handleDelete.bind(this);
    }

    handleDelete() {
        this.props.onShiftDefDelete(this.props.shiftDefId);
    }

    render() {
        return (
            <div>
                <form className="ui form">Name: <input type="text" />
                    {' '}
                    Start: <input type="time" />
                    {' '}
                    End: <input type="time" />
                </form>
                <button className="ui icon button" onClick={this.handleDelete}>
                    <i className="trash icon"></i>
                </button>
            </div>
        );
    }
}