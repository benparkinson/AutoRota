import React from 'react';
import { connect } from 'react-redux';
import { createRota } from '../../actions';
import RotaWizardForm from './RotaWizardForm';

class RotaCreate extends React.Component {
    onSubmit = formProps => {
        this.props.createRota(formProps);
    }

    render() {

        return (
            <div>
                <h3>Create a new Rota</h3>
                <div className="ui container">
                    <RotaWizardForm onSubmit={this.onSubmit} />
                </div>
                {this.props.rotaSubmitMessage}
            </div>
        );
    }
}

const mapStateToProps = ({ rotas }) => {
    return {
        rotaSubmitMessage: rotas.rotaSubmitMessage
    };
}

export default connect(mapStateToProps, { createRota })(RotaCreate);