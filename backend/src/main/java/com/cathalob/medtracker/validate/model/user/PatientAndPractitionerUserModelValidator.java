package com.cathalob.medtracker.validate.model.user;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;

import java.util.List;

public class PatientAndPractitionerUserModelValidator  extends UserModelValidator {
    public PatientAndPractitionerUserModelValidator(UserModel userModel) {
        super(userModel, allowedRoles());
    }

    protected static List<USERROLE> allowedRoles() {
        return List.of(USERROLE.PATIENT, USERROLE.PRACTITIONER);


    }
}