package com.cathalob.medtracker.payload.data;

import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionDetailsData {
    private Long id;
    private int doseMg;
    private Medication medication;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Long patientId;
    private Long practitionerId;
    private List<PrescriptionScheduleEntry> prescriptionScheduleEntries;
}
