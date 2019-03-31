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
            "niceName": "Balance of Average Hours across Doctors",
            "name": "AverageHoursBalance",
            "unique": true,
            "params": [
                {
                    "name": "Weight",
                    "type": "number",
                    "input": "3"
                }
            ]
        },
        {
            "niceName": "Allocate Shifts in Blocks",
            "name": "ShiftBlocks",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "shift",
                    "input": "Night"
                },
                {
                    "name": "DaysInBlock",
                    "type": "text",
                    "input": "friday,saturday,sunday"
                },
                {
                    "name": "Weight",
                    "type": "number",
                    "input": "1"
                },
                {
                    "name": "Force",
                    "type": "checkbox",
                    "tooltip": "Force this block to be assigned every time (warning, too many forced shift blocks can be tricky to work out!)",
                    "input": true
                }
            ]
        },
        {
            "niceName": "Allocate Shifts in Blocks",
            "name": "ShiftBlocks",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "shift",
                    "input": "Night"
                },
                {
                    "name": "DaysInBlock",
                    "type": "text",
                    "input": "monday,tuesday,wednesday,thursday"
                },
                {
                    "name": "Weight",
                    "type": "number",
                    "input": "1"
                },
                {
                    "name": "Force",
                    "type": "checkbox",
                    "tooltip": "Force this block to be assigned every time (warning, too many forced shift blocks can be tricky to work out!)",
                    "input": true
                }
            ]
        },
        {
            "niceName": "Allocate Shifts in Blocks",
            "name": "ShiftBlocks",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "shift",
                    "input": "LongDay"
                },
                {
                    "name": "DaysInBlock",
                    "type": "text",
                    "input": "friday,saturday,sunday"
                },
                {
                    "name": "Weight",
                    "type": "number",
                    "input": "5"
                },
                {
                    "name": "Force",
                    "type": "checkbox",
                    "tooltip": "Force this block to be assigned every time (warning, too many forced shift blocks can be tricky to work out!)",
                    "input": false
                }
            ]
        },
        {
            "niceName": "Balance of Shift Types across Doctors",
            "name": "ShiftTypeBalance",
            "unique": true,
            "params": [
                {
                    "name": "Weight",
                    "type": "number",
                    "input": "3"
                }
            ]
        },
        {
            "niceName": "Avoid Assigning Single Shifts",
            "name": "AvoidSingleShifts",
            "unique": true,
            "params": [
                {
                    "name": "Weight",
                    "type": "number",
                    "input": "2"
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
            "niceName": "Minimum Hours Between Shifts",
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
            "niceName": "Maximum Consecutive Shifts",
            "name": "MaxConsecutiveShifts",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "shift",
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
            "niceName": "Maximum Consecutive Shifts",
            "name": "MaxConsecutiveShifts",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "shift",
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
            "niceName": "Maximum Hours Per Week",
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
            "niceName": "No More Than One Weekend In a Row",
            "name": "NoMoreThanOneWeekendInARow",
            "unique": true,
            "params": [
            ]
        },
        {
            "niceName": "Maximum Average Hours Per Week",
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
            "niceName": "Minimum Hours Break After Consecutive Shifts",
            "name": "MinHoursBreakAfterConsecutiveShifts",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "shift",
                    "input": "LongDay"
                },
                {
                    "name": "MaxConsecutive",
                    "type": "text",
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
            "niceName": "Minimum Hours Break After Consecutive Shifts",
            "name": "MinHoursBreakAfterConsecutiveShifts",
            "unique": false,
            "params": [
                {
                    "name": "ShiftName",
                    "type": "shift",
                    "input": "Night"
                },
                {
                    "name": "MaxConsecutive",
                    "type": "text",
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
    "timeout": 300,
    "name": ""
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