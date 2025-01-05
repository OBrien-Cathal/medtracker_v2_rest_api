package com.cathalob.medtracker.payload.response;

import lombok.*;
@EqualsAndHashCode(callSuper = true)
@Data
public class ApprovePatientRegistrationResponse extends AbstractResponse {
    private Long patientRegistrationId;


}
