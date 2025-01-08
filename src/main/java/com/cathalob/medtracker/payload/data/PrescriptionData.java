package com.cathalob.medtracker.payload.data;

import com.cathalob.medtracker.model.prescription.Medication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class PrescriptionData {
    private Long id;
    private int doseMg;
    private Medication medication;
    private String patientUsername;
    private String practitionerUsername;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
