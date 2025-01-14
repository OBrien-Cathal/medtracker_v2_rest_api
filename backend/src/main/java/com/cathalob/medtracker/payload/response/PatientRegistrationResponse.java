package com.cathalob.medtracker.payload.response;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientRegistrationResponse extends Response {
    private PatientRegistrationData data;


    public PatientRegistrationResponse(boolean b, PatientRegistration reg) {
        super(b);
        this.data = PatientRegistrationData.From(reg);
    }

    public PatientRegistrationResponse(boolean b) {
        super(b);
    }

    public PatientRegistrationResponse() {
    }

    public PatientRegistrationResponse failure(List<String> errors) {
        super.failure(errors);
        return this;
    }

    public static PatientRegistrationResponse Success(PatientRegistration reg) {
        return new PatientRegistrationResponse(true, reg);
    }

    public static PatientRegistrationResponse Failed() {
        return new PatientRegistrationResponse(false);
    }
    public static PatientRegistrationResponse Failed(List<String> errors) {
        return new PatientRegistrationResponse().failure(errors);
    }


}
