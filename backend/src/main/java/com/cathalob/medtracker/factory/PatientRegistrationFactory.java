package com.cathalob.medtracker.factory;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;

import java.time.LocalDateTime;

public class PatientRegistrationFactory {


    public static PatientRegistration PatientRegistration(
            UserModel userModel,
            UserModel practitionerUserModel) {
        return new PatientRegistration(null, userModel, practitionerUserModel, false);

    }

    public PatientRegistration patientRegistration(
            UserModel userModel,
            UserModel practitionerUserModel) {
        return PatientRegistration(userModel, practitionerUserModel);

    }

    public RoleChange roleChange(PatientRegistration patientRegistration) {
        RoleChange roleChange = new RoleChange();
        roleChange.setNewRole(USERROLE.PATIENT);
        roleChange.setUserModel(patientRegistration.getUserModel());
        roleChange.setOldRole(patientRegistration.getUserModel().getRole());
        roleChange.setRequestTime(LocalDateTime.now());
        roleChange.setApprovedBy(patientRegistration.getPractitionerUserModel());
        roleChange.setApprovalTime(LocalDateTime.now());
        return roleChange;
    }

}

