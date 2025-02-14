package com.cathalob.medtracker.validate.model.user;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;

import java.util.List;

public class PractitionerUserModelValidator extends UserModelValidator {
    public PractitionerUserModelValidator(UserModel userModel) {
        super(userModel, allowedRoles());
    }

    protected static List<USERROLE> allowedRoles() {
        return List.of(USERROLE.PRACTITIONER);
    }


}

