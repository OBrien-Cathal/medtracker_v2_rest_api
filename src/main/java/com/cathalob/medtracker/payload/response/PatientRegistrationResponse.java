package com.cathalob.medtracker.payload.response;

import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientRegistrationResponse  extends AbstractResponse{
    private PatientRegistrationData data;
}
