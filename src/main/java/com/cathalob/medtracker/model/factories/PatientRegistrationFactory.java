package com.cathalob.medtracker.model.factories;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;

public class PatientRegistrationFactory {
    public static PatientRegistration PatientRegistration(
            UserModel userModel,
            UserModel practitionerUserModel,
            boolean registered) {
        return new PatientRegistration(null, userModel, practitionerUserModel, registered);

    }

}
