package com.cathalob.medtracker.validate.service;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.validate.model.patient.ApprovePatientRegistrationValidator;
import com.cathalob.medtracker.validate.model.patient.PatientRegistrationValidator;
import com.cathalob.medtracker.validate.model.user.UserModelValidator;

public class PatientServiceValidator extends ServiceValidator {

    public void validateRegisterPatient(PatientRegistration registration, PatientRegistration existingPatientRegistration) {
        PatientRegistrationValidator.aRegisterPatientValidator(
                registration, existingPatientRegistration).validate();
    }

    public void validateApprovePatientRegistration(PatientRegistration registration, UserModel approvingUser) {
        new ApprovePatientRegistrationValidator(
                registration, approvingUser).validate();
    }

    public void validatePatient(UserModel userModel) {
        UserModelValidator.PractitionerUserModelValidator(userModel).validate();
    }
}
