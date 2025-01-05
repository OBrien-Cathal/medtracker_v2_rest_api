package com.cathalob.medtracker.payload.data.factories;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;

public class PatientRegistrationDataFactory {
    public static PatientRegistrationData From(PatientRegistration reg) {
        return new PatientRegistrationData(
                reg.getId(),
                reg.getUserModel().getId(),
                reg.getPractitionerUserModel().getId(),
                reg.isRegistered());
    }
}
