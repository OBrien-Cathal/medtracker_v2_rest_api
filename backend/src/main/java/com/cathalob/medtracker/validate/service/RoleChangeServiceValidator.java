package com.cathalob.medtracker.validate.service;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.validate.actions.ApproveRoleChangeValidator;
import com.cathalob.medtracker.validate.actions.SubmitRoleChangeValidator;

import java.util.List;

public class RoleChangeServiceValidator extends ServiceValidator {
    public void validateSubmitRoleChange(RoleChange roleChange, List<RoleChange> unapproved) {
         new SubmitRoleChangeValidator(roleChange, unapproved).validate();

    }

    public void validateApproveRoleChange(RoleChange roleChange, UserModel approvingUser) {
         new ApproveRoleChangeValidator(roleChange, approvingUser).validate();

    }

}
