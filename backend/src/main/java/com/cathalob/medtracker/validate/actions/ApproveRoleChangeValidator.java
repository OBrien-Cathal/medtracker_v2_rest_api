package com.cathalob.medtracker.validate.actions;

import com.cathalob.medtracker.exception.validation.ApproveRoleChangeValidationException;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;

public class ApproveRoleChangeValidator extends ActionValidator {


    private final RoleChange roleChange;
    private final UserModel approvingUser;

    public ApproveRoleChangeValidator(RoleChange roleChange, UserModel approvingUser) {
        super();
        this.roleChange = roleChange;
        this.approvingUser = approvingUser;
    }

    @Override
    protected void basicValidate() {

        validateObjectPresence(roleChange);

        if (!approvingUser.getRole().equals(USERROLE.ADMIN)){
            addError("Insufficient privileges to approve role change");
        }
        if(validationFailed()) return;

        if (roleChange.getApprovedBy() != null)
            addError(String.format(
                    "Role change with Id: %s was already approved by: %s at: %s",
                    roleChange.getId(),
                    roleChange.getApprovedBy().getUsername(),
                    roleChange.getApprovalTime().toString()));
        if (roleChange.getUserModel().getRole() != USERROLE.USER) {
            addError(String.format("User Role: %s is not a candidate for role change to: %s",
                    roleChange.getUserModel().getRole().name(),
                    roleChange.getNewRole()));
        }
    }

    @Override
    protected void raiseValidationException() {
        throw new ApproveRoleChangeValidationException(getErrors());
    }
}
