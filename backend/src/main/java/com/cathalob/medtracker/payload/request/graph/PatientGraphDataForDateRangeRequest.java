package com.cathalob.medtracker.payload.request.graph;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor

@NoArgsConstructor

public class PatientGraphDataForDateRangeRequest extends GraphDataForDateRangeRequest {
    private Long patientId;

    public PatientGraphDataForDateRangeRequest(Long patientId, LocalDate start, LocalDate end) {
        super(start, end);
        this.patientId = patientId;
    }
}
