import React from 'react';
import { ShiftDefinitionRow } from './ShiftDefinitionRow';
import Card from '../components/Card';

export class ShiftDefinitionContainer extends React.Component {
    state = {
        shiftDefs: []
    }

    constructor(props) {
        super(props);

        this.nextShiftDefId = 0;

        this.handleAddShiftDef = this.handleAddShiftDef.bind(this);
        this.handleDeleteShiftDef = this.handleDeleteShiftDef.bind(this);
    }

    createNewShiftDef() {
        return this.nextShiftDefId++;
    }

    handleAddShiftDef() {
        const shiftDef = this.createNewShiftDef();
        this.setState((prevState) => ({
            shiftDefs: prevState.shiftDefs.concat([shiftDef])
        }));
    }

    handleDeleteShiftDef(shiftDefId) {
        this.setState((prevState) => ({
            shiftDefs: prevState.shiftDefs.filter(function (shiftDef) {
                return shiftDef !== shiftDefId
            }
            )
        }));
    }

    render() {
        const shiftDefItems = this.state.shiftDefs.map((x) =>
            <div className="column" key={x}>
                <Card >
                    <ShiftDefinitionRow onShiftDefDelete={this.handleDeleteShiftDef} shiftDefId={x} ></ShiftDefinitionRow>
                </Card>
            </div>
        );
        return (
            <div className="ui grid container">
                <h4>Shift Definitions:</h4>
                <div className="four column row">
                    {shiftDefItems}
                </div>
                <button className="ui icon button" onClick={this.handleAddShiftDef}>
                    <i className="add icon"></i>
                </button>
            </div >
        );
    }
}