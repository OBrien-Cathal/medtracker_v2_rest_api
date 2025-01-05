package com.cathalob.medtracker.payload.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PatientRegistrationData {
    private Long practitionerId;
    private Long userModelId;
    private Long id;
    private boolean approved;
}
