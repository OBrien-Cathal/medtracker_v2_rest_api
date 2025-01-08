package com.cathalob.medtracker.payload.data;

import com.cathalob.medtracker.model.PatientRegistration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PatientRegistrationData {
    private Long id;
    private Long userModelId;
    private Long practitionerId;
    private boolean approved;

    public static PatientRegistrationData From(PatientRegistration reg) {
        return new PatientRegistrationData(
                reg.getId(),
                reg.getUserModel().getId(),
                reg.getPractitionerUserModel().getId(),
                reg.isRegistered());
    }

}
