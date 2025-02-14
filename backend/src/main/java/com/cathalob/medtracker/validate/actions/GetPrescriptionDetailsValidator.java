package com.cathalob.medtracker.validate.actions;

import com.cathalob.medtracker.exception.validation.PrescriptionValidatorException;
import com.cathalob.medtracker.exception.validation.UserModelValidationException;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.validate.model.user.UserModelValidator;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GetPrescriptionDetailsValidator extends ActionValidator {
    private Prescription prescription;
    private UserModel requestingUser;
    private List<PatientRegistration> patientRegistrationList;

    @Override
    protected void basicValidate() {

        if (prescription == null)
            addError("Prescription with provided ID does not exist");
        try {
            UserModelValidator.PatientAndPractitionerUserModelValidator(requestingUser).validate();
        } catch (UserModelValidationException e) {
            addErrors(e.getErrors());
        }

        if (requestingUser.getRole().equals(USERROLE.PRACTITIONER) && patientRegistrationList.isEmpty())
            addError("Not allowed to view prescriptions of unregistered patient");
    }

    @Override
    protected void raiseValidationException() {
        throw new PrescriptionValidatorException(getErrors());
    }
}
