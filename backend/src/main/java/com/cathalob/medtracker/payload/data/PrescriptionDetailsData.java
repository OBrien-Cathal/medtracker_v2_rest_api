package com.cathalob.medtracker.payload.data;

import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class PrescriptionDetailsData {
    private Long id;
    private int doseMg;
    private Long medicationId;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Long patientId;
    private Long practitionerId;
    private PrescriptionOverviewData prescriptionOverviewData;
    private List<PrescriptionScheduleEntry> prescriptionScheduleEntry;
}
