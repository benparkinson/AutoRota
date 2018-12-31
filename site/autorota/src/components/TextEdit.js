import React from 'react';

export class TextEdit extends React.Component {
    constructor(props) {
        super(props);
        this.state = { value: '' };
        this.handleChange = this.handleChange.bind(this);
    }

    handleChange(e) {
        let val;
        if (e.target.value == null) {
            val = '';
        } else {
            val = e.target.value;
        }
        this.setState({ value: val });
    }

    render() {

        return (
            <div className="SplitPane">
                <div className="SplitPane-left">
                    <input type="text"
                        value={this.state.value}
                        placeholder={this.props.placeholder}
                        onChange={this.handleChange} />
                </div>
                <div className="SplitPane-right">
                    <h1>Your name is: {this.state.value.toLowerCase()}</h1>
                </div>
            </div>);
    }
}