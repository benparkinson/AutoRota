import React from 'react';
import { connect } from 'react-redux';
import { reduxForm } from 'redux-form';
import { fetchRotaSoftRules } from '../../../actions';
import validateRotaForm from './validateRotaForm';
import RuleFormPage from './RuleFormPage';

class SoftRuleFormPage extends React.Component {
    componentDidMount() {
        this.props.fetchRotaSoftRules();
    }

    render() {
        return (
            <form onSubmit={this.props.handleSubmit} className="ui form error">
                <RuleFormPage name="softRules" possibleRules={this.props.possibleRules} />
                {this.props.backButton}
                {this.props.submitButton}
            </form>
        );
    }
}

const mapStateToProps = ({ rota }) => {
    return {
        possibleRules: rota.softRules
    };
}

const ConnectedForm = connect(mapStateToProps, { fetchRotaSoftRules })(SoftRuleFormPage);

export default reduxForm({
    form: 'rotaWizard',
    destroyOnUnmount: false,
    forceUnregisterOnUnmount: true,
    validate: validateRotaForm
})(ConnectedForm);