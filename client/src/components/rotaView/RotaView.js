import React from 'react';
import { connect } from 'react-redux';
import { viewRota } from '../../actions';

class RotaView extends React.Component {
    componentDidMount() {
        this.props.viewRota();
    }

    render() {
        if (!this.props.rota)
            return <div>No rota found, please create one or refresh this page if waiting on one to finish.</div>;

        return (
            <div>
                {this.props.rota.split('\n').map((line, key) => {
                    return <React.Fragment key={key}>{line}<br /></React.Fragment>
                })}
            </div>
        );
    }
}

const mapStateToProps = ({ rota }) => {
    return {
        rota: rota.latestRota
    };
}

export default connect(mapStateToProps, { viewRota })(RotaView);