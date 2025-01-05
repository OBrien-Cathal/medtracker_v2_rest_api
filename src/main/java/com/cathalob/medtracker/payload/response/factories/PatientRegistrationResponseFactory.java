package com.cathalob.medtracker.payload.response.factories;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.payload.data.factories.PatientRegistrationDataFactory;
import com.cathalob.medtracker.payload.response.PatientRegistrationResponse;

import java.util.List;

public class PatientRegistrationResponseFactory {

    public static PatientRegistrationResponse Successful(PatientRegistration reg) {
        PatientRegistrationResponse response = new PatientRegistrationResponse();
        AbstractResponseFactory.fillSuccessfulDefaults(response);
        response.setData(PatientRegistrationDataFactory.From(reg));
        return response;
    }

    public static PatientRegistrationResponse Failed(List<String> errors) {
        PatientRegistrationResponse response = new PatientRegistrationResponse();
        AbstractResponseFactory.fillFailedDefaults(response, errors);
        return response;
    }
}
