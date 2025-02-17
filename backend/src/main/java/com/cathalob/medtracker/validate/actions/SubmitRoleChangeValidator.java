package com.cathalob.medtracker.validate.actions;

import com.cathalob.medtracker.exception.validation.SubmitRoleChangeValidatorException;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;


import java.util.List;

public class SubmitRoleChangeValidator extends ActionValidator {


    private final RoleChange roleChange;
    private final List<RoleChange> unapproved;

    public SubmitRoleChangeValidator(RoleChange roleChange, List<RoleChange> unapproved) {
        super();
        this.roleChange = roleChange;
        this.unapproved = unapproved;
    }

    private void validateRoleChangeUser() {
        if (roleChange.getUserModel().getRole() != USERROLE.USER) {
            addError(String.format("Current User Role: %s is not a candidate for role change to: %s",
                    roleChange.getUserModel().getRole().name(),
                    roleChange.getNewRole()));
        }
    }

    @Override
    protected void basicValidate() {
        validateRoleChangeUser();
        if (validationFailed()) return;
        if (!unapproved.isEmpty()) {
            addError("Unapproved request already submitted for role: " + roleChange.getNewRole().name() + ")");
        }
    }


    @Override
    protected void raiseValidationException() {
        throw new SubmitRoleChangeValidatorException(getErrors());
    }
}
