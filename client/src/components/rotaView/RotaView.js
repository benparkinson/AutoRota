import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import Table from 'react-bootstrap/Table';

import { STATUS_ERROR, STATUS_IN_PROGRESS } from './constants';
import { fetchRota } from '../../actions';

class RotaView extends React.Component {
    componentDidMount() {
        const rotaId = this.props.match.params.id;
        this.props.fetchRota(rotaId);
    }

    renderContent = () => {
        const status = this.props.rota.status;

        if (status === STATUS_IN_PROGRESS) {
            return (
                <div className="ui padded loading segment">
                    Rota still in progress...please wait...
                </div>
            );
        }

        if (status === STATUS_ERROR) {
            return (
                <div className="ui tertiary inverted red segment">
                    <i className="warning icon"></i>
                    Oh no...something went wrong with your rota...sorry!
                </div>
            );
        }

        return (
            <div className="ui raised segment">
                {this.renderRota()}
            </div >
        );
    }

    renderRota = () => {
        const rota = this.props.rota;

        const lines = rota.stringRepresentation.split('\n');

        const header = lines[0].split(',').map((h, index) => {
            return (
                <th key={index}>{h}</th>
            );
        });

        lines.shift(); // remove header line

        const body = lines.map((line, index) => {
            const cells = line.split(',').map((cell, index) => {
                if (index === 0) {
                    return (
                        <th key={index}>{cell}</th>
                    );
                }
                return (
                    <td key={index}>{cell}</td>
                );
            });

            return (
                <tr key={index}>{cells}</tr>
            );
        })

        return (
            <Table striped bordered hover responsive size="sm">
                <thead>
                    <tr>
                        {header}
                    </tr>
                </thead>
                <tbody>
                    {body}
                </tbody>
            </Table>
        );
    }

    renderAllRotasButton = () => {
        return (
            <>
                <Link to="/rotas">
                    <button className="ui labeled icon secondary button">
                        <i className="left chevron icon"></i>
                        All Rotas
                    </button>
                </Link>
            </>
        );
    }

    render() {
        if (!this.props.rota)
            return <div>No rota found!</div>;

        return (
            <div>
                {this.renderAllRotasButton()}
                <h4 className="ui header">Rota {this.props.rota.id}:</h4>
                {this.renderContent()}
            </div>
        );
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        rota: state.rotas[ownProps.match.params.id]
    };
}

export default connect(mapStateToProps, { fetchRota })(RotaView);