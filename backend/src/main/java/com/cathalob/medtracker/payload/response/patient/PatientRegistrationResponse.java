package com.cathalob.medtracker.payload.response.patient;

import com.cathalob.medtracker.payload.response.generic.Response;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientRegistrationResponse extends Response {
    private Long registrationId;

    public PatientRegistrationResponse(ResponseInfo responseInfo, Long patientRegistration) {
        super(responseInfo);
        this.registrationId = patientRegistration;
    }

    public PatientRegistrationResponse(ResponseInfo responseInfo) {
        super(responseInfo);
    }

    public static PatientRegistrationResponse Success(Long reg) {
        return new PatientRegistrationResponse(ResponseInfo.Success(), reg);
    }

    public static PatientRegistrationResponse Failed() {
        return new PatientRegistrationResponse(ResponseInfo.Failed());
    }

    public static PatientRegistrationResponse Failed(List<String> errors) {
        return new PatientRegistrationResponse(ResponseInfo.Failed(errors));
    }


}
