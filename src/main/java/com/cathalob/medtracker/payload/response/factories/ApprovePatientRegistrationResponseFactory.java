package com.cathalob.medtracker.payload.response.factories;

import com.cathalob.medtracker.payload.response.ApprovePatientRegistrationResponse;

import java.util.List;

public class ApprovePatientRegistrationResponseFactory extends AbstractResponseFactory {

   public static ApprovePatientRegistrationResponse Successful(Long patientRegId) {
        ApprovePatientRegistrationResponse response = new ApprovePatientRegistrationResponse();
        AbstractResponseFactory.fillSuccessfulDefaults(response);
        return response;
    }

    public static ApprovePatientRegistrationResponse Failed(List<String> errors) {
        ApprovePatientRegistrationResponse response = new ApprovePatientRegistrationResponse();
        AbstractResponseFactory.fillFailedDefaults(response, errors);
        return response;
    }
}
