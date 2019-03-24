import React from 'react';
import converter from 'number-to-words';
import { reduxForm } from 'redux-form';

import validateRotaForm from './formPages/validateRotaForm';
import GeneralConfigFormPage from './formPages/GeneralConfigFormPage';
import DoctorFormPage from './formPages/DoctorFormPage';
import HardRuleFormPage from './formPages/HardRuleFormPage';
import ShiftDefinitionFormPage from './formPages/ShiftDefinitionFormPage';
import SoftRuleFormPage from './formPages/SoftRuleFormPage';

const defaultFormValues = {
    "softRules": [
        {
            "name": "AverageHoursBalance",
            "unique": true,
            "params": [
                {
                    "name": "Weight",
                    "type": "number",
                    "input": "6"
                }
            ]
        },
        {
            "name": "ShiftBlocks",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "string",
                    "input": "Night"
                },
                {
                    "name": "DaysInBlock",
                    "type": "string",
                    "input": "friday,saturday,sunday"
                },
                {
                    "name": "Weight",
                    "type": "number",
                    "input": "1"
                }
            ]
        },
        {
            "name": "ShiftBlocks",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "string",
                    "input": "Night"
                },
                {
                    "name": "DaysInBlock",
                    "type": "string",
                    "input": "monday,tuesday,wednesday,thursday"
                },
                {
                    "name": "Weight",
                    "type": "number",
                    "input": "1"
                }
            ]
        },
        {
            "name": "ShiftBlocks",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "string",
                    "input": "LongDay"
                },
                {
                    "name": "DaysInBlock",
                    "type": "string",
                    "input": "friday,saturday,sunday"
                },
                {
                    "name": "Weight",
                    "type": "number",
                    "input": "1"
                }
            ]
        },
        {
            "name": "ShiftTypeBalance",
            "unique": true,
            "params": [
                {
                    "name": "Weight",
                    "type": "number",
                    "input": "4"
                }
            ]
        }
    ],
    "doctors": [
        {
            "name": "Doctor Ben"
        },
        {
            "name": "Doctor Dee"
        },
        {
            "name": "Doctor BPark"
        },
        {
            "name": "Doctor Sasha"
        },
        {
            "name": "Doctor Competent"
        },
        {
            "name": "Suede"
        },
        {
            "name": "Hercule Muller"
        },
        {
            "name": "Doctor Test"
        },
        {
            "name": "The Doctor"
        },
        {
            "name": "Doctor Octopus"
        },
        {
            "name": "Doctor Doom"
        },
        {
            "name": "Doctor Doctor"
        }
    ],
    "hardRules": [
        {
            "name": "MinHoursBetweenShifts",
            "unique": true,
            "params": [
                {
                    "name": "MinHours",
                    "type": "number",
                    "input": "11"
                }
            ]
        },
        {
            "name": "MaxConsecutiveShifts",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "string",
                    "input": "LongDay"
                },
                {
                    "name": "MaxConsecutive",
                    "type": "number",
                    "input": "5"
                }
            ]
        },
        {
            "name": "MaxConsecutiveShifts",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "string",
                    "input": "Night"
                },
                {
                    "name": "MaxConsecutive",
                    "type": "number",
                    "input": "4"
                }
            ]
        },
        {
            "name": "MaxHoursPerWeek",
            "unique": true,
            "params": [
                {
                    "name": "MaxHours",
                    "type": "number",
                    "input": "72"
                }
            ]
        },
        {
            "name": "NoMoreThanOneWeekendInARow",
            "unique": true,
            "params": [
            ]
        },
        {
            "name": "MaxAverageHoursPerWeek",
            "unique": true,
            "params": [
                {
                    "name": "MaxHours",
                    "type": "number",
                    "input": "48"
                }
            ]
        },
        {
            "name": "MinHoursBreakAfterConsecutiveShifts",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "string",
                    "input": "LongDay"
                },
                {
                    "name": "MaxConsecutive",
                    "type": "string",
                    "input": "4"
                },
                {
                    "name": "MinHours",
                    "type": "number",
                    "input": "48"
                }
            ]
        },
        {
            "name": "MinHoursBreakAfterConsecutiveShifts",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "string",
                    "input": "Night"
                },
                {
                    "name": "MaxConsecutive",
                    "type": "string",
                    "input": "3,4"
                },
                {
                    "name": "MinHours",
                    "type": "number",
                    "input": "46"
                }
            ]
        }
    ],
    "shiftDefinitions": [
        {
            "shiftName": "Day",
            "shiftStart": "08:30",
            "shiftEnd": "17:00",
            "dayRequirements": {
                "monday": "2",
                "tuesday": "2",
                "wednesday": "2",
                "thursday": "2",
                "friday": "2",
                "saturday": "2",
                "sunday": "2"
            }
        },
        {
            "shiftName": "LongDay",
            "shiftStart": "08:30",
            "shiftEnd": "21:00",
            "dayRequirements": {
                "monday": "2",
                "tuesday": "2",
                "wednesday": "2",
                "thursday": "2",
                "friday": "2",
                "saturday": "2",
                "sunday": "2"
            }
        },
        {
            "shiftName": "Night",
            "shiftStart": "20:30",
            "shiftEnd": "09:00",
            "dayRequirements": {
                "monday": "2",
                "tuesday": "2",
                "wednesday": "2",
                "thursday": "2",
                "friday": "2",
                "saturday": "2",
                "sunday": "2"
            }
        }
    ],
    "startDate": "2019-01-07",
    "endDate": "2019-04-01",
    "timeout": 300
}

const pageOrder = [
    {
        comp: ShiftDefinitionFormPage,
        name: 'Define Shifts'
    },
    {
        comp: HardRuleFormPage,
        name: 'Set Hard Rules'
    },
    {
        comp: SoftRuleFormPage,
        name: 'Set Soft Rules'
    },
    {
        comp: DoctorFormPage,
        name: 'Define Doctors'
    },
    {
        comp: GeneralConfigFormPage,
        name: 'Final Configuration'
    }
];

class RotaWizardForm extends React.Component {

    state = {
        currentPage: 0
    }

    renderPageNames = () => {
        return pageOrder.map(({ name }, index) => {
            let buttonType;
            if (this.state.currentPage === index) {
                buttonType = 'black';
            } else {
                buttonType = '';
            }
            return (
                <button key={index} className={`ui ${buttonType} basic button`} onClick={() => this.setCurrentPage(index)}>
                    {name}
                </button>
            );
        });
    }

    renderSubmitContent = () => {
        const submitContent = this.state.currentPage === pageOrder.length - 1 ? 'Submit' : <i className="arrow circle right icon" />;
        const className = this.state.currentPage === pageOrder.length - 1 ? "ui button field primary right floated" : "ui icon basic button right floated";
        return (
            <button type="submit" className={className} >
                {submitContent}
            </button>
        );
    }

    renderBackButton = (handleBack) => {
        const fieldName = this.state.currentPage === 0 ? "ui icon field basic disabled button" : "ui icon basic button";
        return (
            <button type="button" className={fieldName} onClick={handleBack}>
                <i className="arrow circle left icon" />
            </button>
        );
    }

    setCurrentPage = (page) => {
        this.setState({
            currentPage: page
        });
    }

    render() {
        const FormPage = pageOrder[this.state.currentPage].comp;
        const pageNames = this.renderPageNames();
        const pageCount = converter.toWords(pageOrder.length);
        const handleSubmit = this.state.currentPage === pageOrder.length - 1 ? this.props.onSubmit : () => this.setCurrentPage(this.state.currentPage + 1)
        const handleBack = this.state.currentPage === 0 ? null : () => this.setCurrentPage(this.state.currentPage - 1)
        const submitButton = this.renderSubmitContent();
        const backButton = this.renderBackButton(handleBack);

        return (
            <div>
                <div className={`${pageCount} ui basic buttons`}>
                    {pageNames}
                </div>
                <FormPage onSubmit={handleSubmit} submitButton={submitButton} backButton={backButton}
                    initialValues={defaultFormValues} />
            </div>
        );
    }
}

export default reduxForm({
    form: 'rotaWizard',
    destroyOnUnmount: false,
    forceUnregisterOnUnmount: true,
    validateRotaForm
})(RotaWizardForm);