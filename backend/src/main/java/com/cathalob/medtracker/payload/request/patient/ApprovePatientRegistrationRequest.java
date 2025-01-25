package com.cathalob.medtracker.payload.request.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovePatientRegistrationRequest {
    private Long patientRegistrationId;
}
