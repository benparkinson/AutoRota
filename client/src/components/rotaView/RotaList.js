import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchRotas } from '../../actions';
import { STATUS_COMPLETE, STATUS_ERROR, STATUS_IN_PROGRESS } from './constants';

class RotaList extends React.Component {
    componentDidMount() {
        this.props.fetchRotas();
    }

    getIconClassName = (status) => {
        if (status === STATUS_COMPLETE) {
            return "calendar check outline";
        }
        if (status === STATUS_ERROR) {
            return "calendar times outline";
        }
        if (status === STATUS_IN_PROGRESS) {
            return "hourglass half";
        }

        return "";
    }

    renderListPlaceholder = () => {
        return (
            <div class="ui placeholder segment">
                <div class="ui icon header">
                    <i class="exclamation icon"></i>
                    No rotas created!
                </div>
                <Link to="/rotas/new" className="ui button primary">
                    Create a Rota
                </Link>
            </div>
        );
    }

    renderRotaList = () => {
        if (!this.props.rotas || this.props.rotas.length === 0) {
            return this.renderListPlaceholder();
        }

        return this.props.rotas.map(rota => {
            const iconClassName = this.getIconClassName(rota.status);
            return (

                <div className="item" key={rota.id}>
                    <i className={`large middle aligned icon ${iconClassName}`} />
                    <div className="content">
                        <Link to={`/rotas/${rota.id}`}>
                            {rota.id}
                        </Link>
                        <div className="description">
                            Status: {rota.status}
                            <br />
                            Requested at: {rota.startDateTime}
                        </div>
                    </div>
                </div>
            );
        });
    }

    render() {
        return (
            <div>
                <h2 className="ui header">Rotas</h2>
                <div className="ui celled list">
                    {this.renderRotaList()}
                </div>
            </div>
        );
    }
}

const mapStateToProps = ({ rotas }) => {
    return {
        rotas: Object.values(rotas)
    }
}

export default connect(mapStateToProps, { fetchRotas })(RotaList);