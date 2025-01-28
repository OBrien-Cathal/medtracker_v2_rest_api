package com.cathalob.medtracker.payload.request.patient;

import com.cathalob.medtracker.payload.data.BloodPressureData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatedBloodPressureDataRequest {
    private LocalDate date;
    private BloodPressureData bloodPressureData;
}
