package com.cathalob.medtracker.validate.model.patient;

import com.cathalob.medtracker.exception.validation.ApprovePatientRegistrationValidatorException;
import com.cathalob.medtracker.exception.validation.UserModelValidationException;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.validate.actions.ActionValidator;
import com.cathalob.medtracker.validate.model.user.UserModelValidator;

public class ApprovePatientRegistrationValidator extends ActionValidator {
    private PatientRegistration patientRegistration;
    private UserModel approvingUser;

    public ApprovePatientRegistrationValidator(PatientRegistration patientRegistration, UserModel approvingUser) {
        this.patientRegistration = patientRegistration;
        this.approvingUser = approvingUser;
    }

    @Override
    protected void basicValidate() {
        validateObjectPresence(patientRegistration);
        validateApprovingUserModel();

        if (patientRegistration.isRegistered()){
            addError("Registration is already approved");
        }
        validateRegisteringUser();
    }

    private void validateRegisteringUser() {
        try {
            UserModelValidator.PatientAndUserUserModelValidator(patientRegistration.getUserModel()).validate();
        } catch (UserModelValidationException e) {
            addErrors(e.getErrors());
        }
    }

    private void validateApprovingUserModel() {
        try {
            UserModelValidator.PractitionerUserModelValidator(approvingUser).validate();
        } catch (UserModelValidationException e) {
            addErrors(e.getErrors());
        }
        if (validationFailed()) return;
        if (!patientRegistration.getPractitionerUserModel().getId().equals(approvingUser.getId())) {
            addError("Only possible to approve own registrations");
        }
    }

    @Override
    protected void raiseValidationException() {
        throw new ApprovePatientRegistrationValidatorException(getErrors());
    }

}
