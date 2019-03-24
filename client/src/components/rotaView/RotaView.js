import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { STATUS_COMPLETE, STATUS_ERROR, STATUS_IN_PROGRESS } from './constants';

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
                {this.props.rota.stringRepresentation.split('\n').map((line, key) => {
                    return <React.Fragment key={key}>{line}<br /></React.Fragment>
                })}
            </div >
        );
    }

    renderAllRotasButton = () => {
        return (
            <>
                <Link to="/rotas">
                    <button className="ui labeled icon button">
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
                <h3>Rota {this.props.rota.id}:</h3>
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