import React from 'react';
import { connect } from 'react-redux';
import { reduxForm } from 'redux-form';
import { fetchRotaHardRules } from '../../../actions';
import validateRotaForm from './validateRotaForm';
import RuleFormPage from './RuleFormPage';

class HardRuleFormPage extends React.Component {
    componentDidMount() {
        this.props.fetchRotaHardRules();
    }

    render() {
        return (
            <form onSubmit={this.props.handleSubmit} className="ui form error">
                <RuleFormPage name="hardRules" possibleRules={this.props.possibleRules} shiftTypes={this.props.shiftTypes} />
                {this.props.backButton}
                {this.props.submitButton}
            </form>
        );
    }
}

const mapStateToProps = ({ rules, form }) => {
    const { shiftDefinitions } = form.rotaWizard.values;
    const shiftTypes = shiftDefinitions.map(shift => shift.shiftName);
    return {
        possibleRules: rules.hardRules,
        shiftTypes
    };
}

const ConnectedForm = connect(mapStateToProps, { fetchRotaHardRules })(HardRuleFormPage);

export default reduxForm({
    form: 'rotaWizard',
    destroyOnUnmount: false,
    forceUnregisterOnUnmount: true,
    validate: validateRotaForm
})(ConnectedForm);