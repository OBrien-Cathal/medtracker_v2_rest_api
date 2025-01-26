package com.cathalob.medtracker.payload.response.patient;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import com.cathalob.medtracker.payload.response.generic.Response;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientRegistrationResponse extends Response {
    private PatientRegistrationData data;

    public PatientRegistrationResponse(ResponseInfo responseInfo, PatientRegistration patientRegistration) {
        super(responseInfo);
        this.data = PatientRegistrationData.From(patientRegistration);
    }

    public PatientRegistrationResponse(ResponseInfo responseInfo) {
        super(responseInfo);
    }

    public static PatientRegistrationResponse Success(PatientRegistration reg) {
        return new PatientRegistrationResponse(ResponseInfo.Success(), reg);
    }

    public static PatientRegistrationResponse Failed() {
        return new PatientRegistrationResponse(ResponseInfo.Failed());
    }

    public static PatientRegistrationResponse Failed(List<String> errors) {
        return new PatientRegistrationResponse(ResponseInfo.Failed(errors));
    }


}
