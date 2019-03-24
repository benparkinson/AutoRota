import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import ProgressBar from 'react-bootstrap/ProgressBar';
import moment from 'moment';

import { fetchRotas } from '../../actions';
import { STATUS_COMPLETE, STATUS_ERROR, STATUS_IN_PROGRESS } from './constants';

class RotaList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            time: moment.utc()
        };
    }

    componentDidMount() {
        this.props.fetchRotas();
    }

    componentDidUpdate() {
        if (!this.props.rotas || this.props.rotas.length === 0) {
            return;
        }

        if (this.props.rotas.find(rota => rota.status === STATUS_IN_PROGRESS)) {
            if (!this.interval) {
                this.interval = setInterval(() =>
                    this.setState({
                        time: moment.utc()
                    }), 1000);
            }
        } else {
            this.tearDownInterval();
        }
    }

    tearDownInterval = () => {
        if (this.interval) {
            clearInterval(this.interval);
        }
    }

    componentWillUnmount() {
        this.tearDownInterval();
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
            <div className="ui placeholder segment">
                <div className="ui icon header">
                    <i className="exclamation icon"></i>
                    No rotas created!
                </div>
                <Link to="/rotas/new" className="ui button primary">
                    Create a Rota
                </Link>
            </div>
        );
    }

    renderProgressBar = (rota) => {
        if (rota.status !== STATUS_IN_PROGRESS) {
            return null;
        }

        const now = this.state.time;
        const start = moment.utc(rota.startDateTime);
        const progress = (now - start) / 1000;

        if (progress > rota.timeoutSeconds + 1) {
            this.props.fetchRotas();
        }

        return (
            <ProgressBar animated variant="success" now={progress} max={rota.timeoutSeconds} />
        );
    }

    renderDescription = (rota) => {
        const start = moment.parseZone(rota.startDateTime).local().format('HH:mm:ss, DD/MM/YY');
        const end = moment.parseZone(rota.endDateTime).local().format('HH:mm:ss, DD/MM/YY');
        var desc = `Requested at: ${start}`;
        if (rota.endDateTime) {
            desc += `, completed at ${end}`;
        }
        return desc;
    }

    renderRotaList = () => {
        if (!this.props.rotas || this.props.rotas.length === 0) {
            return this.renderListPlaceholder();
        }

        return this.props.rotas.map(rota => {
            const iconClassName = this.getIconClassName(rota.status);
            const description = this.renderDescription(rota);
            return (
                <div className="item" key={rota.id}>
                    <div className="right floated content">
                        Status: {rota.status}
                    </div>
                    <i className={`large middle aligned icon ${iconClassName}`} />
                    <div className="content">
                        <Link to={`/rotas/${rota.id}`}>
                            {rota.id}
                        </Link>
                        {this.renderProgressBar(rota)}
                        <div className="description">
                            {description}
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
                <div className="ui large middle aligned celled list">
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