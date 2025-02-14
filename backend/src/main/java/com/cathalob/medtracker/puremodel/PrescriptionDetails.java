package com.cathalob.medtracker.puremodel;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PrescriptionDetails {

    private Prescription prescription;
    private List<PrescriptionScheduleEntry> prescriptionScheduleEntries;


}
