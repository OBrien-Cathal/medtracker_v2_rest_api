package com.cathalob.medtracker.puremodel;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.prescription.Prescription;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PrescriptionImport {
    private Long medicationId;
    private Long patientId;
    private String practitionerUserName;
    private Prescription prescription;
    private List<DAYSTAGE> dayStageList;

}
