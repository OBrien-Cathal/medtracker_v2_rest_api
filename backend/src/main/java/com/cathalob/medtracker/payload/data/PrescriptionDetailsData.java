package com.cathalob.medtracker.payload.data;

import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private int doseMg;
    @NotNull
    private Medication medication;
    @NotNull
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    @NotNull
    private Long patientId;
    private Long practitionerId;
    private List<PrescriptionScheduleEntry> prescriptionScheduleEntries;
}
