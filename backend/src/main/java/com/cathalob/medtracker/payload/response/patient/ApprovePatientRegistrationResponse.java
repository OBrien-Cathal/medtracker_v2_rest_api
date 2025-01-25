package com.cathalob.medtracker.payload.response.patient;

import com.cathalob.medtracker.payload.response.Response;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApprovePatientRegistrationResponse extends Response {
    private Long patientRegistrationId;

    public ApprovePatientRegistrationResponse(boolean b, Long patientRegistrationId) {
        super(b);
        this.patientRegistrationId = patientRegistrationId;
    }

    public ApprovePatientRegistrationResponse(boolean b, Long patientRegistrationId, List<String> errors) {
        super(b, errors);
        this.patientRegistrationId = patientRegistrationId;
    }

    public static ApprovePatientRegistrationResponse Success(Long patientRegistrationId) {
        return new ApprovePatientRegistrationResponse(true, patientRegistrationId);
    }

    public static ApprovePatientRegistrationResponse Failed(Long patientRegistrationId) {
        return new ApprovePatientRegistrationResponse(false, patientRegistrationId);
    }

    public static ApprovePatientRegistrationResponse Failed(Long patientRegistrationId, List<String> errors) {
        return new ApprovePatientRegistrationResponse(false, patientRegistrationId, errors);
    }
}
