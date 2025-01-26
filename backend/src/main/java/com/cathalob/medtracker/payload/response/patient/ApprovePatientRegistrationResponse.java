package com.cathalob.medtracker.payload.response.patient;

import com.cathalob.medtracker.payload.response.generic.Response;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApprovePatientRegistrationResponse extends Response {
    private Long patientRegistrationId;

    public ApprovePatientRegistrationResponse(Long patientRegistrationId, ResponseInfo responseInfo) {
        super(responseInfo);
        this.patientRegistrationId = patientRegistrationId;
    }

    public static ApprovePatientRegistrationResponse Success(Long patientRegistrationId) {
        return new ApprovePatientRegistrationResponse(patientRegistrationId, ResponseInfo.Success());
    }

    public static ApprovePatientRegistrationResponse Failed(Long patientRegistrationId) {
        return new ApprovePatientRegistrationResponse(patientRegistrationId, ResponseInfo.Failed());
    }

    public static ApprovePatientRegistrationResponse Failed(Long patientRegistrationId, List<String> errors) {
        return new ApprovePatientRegistrationResponse(patientRegistrationId, ResponseInfo.Failed(errors));
    }
}
