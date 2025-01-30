package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.enums.USERROLE;

import com.cathalob.medtracker.validate.Validator;
import com.cathalob.medtracker.validate.model.errors.UserRoleError;

import java.util.List;

public class UserRoleValidator extends Validator {
    private final USERROLE userRole;

    public UserRoleValidator(USERROLE userRole) {
        super();
        this.userRole = userRole;
    }

    public Validator validateIsPatient() {
        return validateRoleInAllowed(List.of(USERROLE.PATIENT));

    }

    public Validator validateIsAdmin() {
        return validateRoleInAllowed(List.of(USERROLE.ADMIN));
    }

    public Validator validateIsPractitioner() {
        return validateRoleInAllowed(List.of(USERROLE.PRACTITIONER));
    }

    public Validator validateIsPatientOrPractitioner() {
        return validateRoleInAllowed(List.of(USERROLE.PATIENT, USERROLE.PRACTITIONER));
    }

    private Validator validateRoleInAllowed(List<USERROLE> allowedRoles) {
        if (!allowedRoles.contains(userRole)) {
            this.addError(UserRoleError.of(userRole, allowedRoles));
            System.out.println(this.getErrors().stream().findAny());
        }
        return this;
    }


}
