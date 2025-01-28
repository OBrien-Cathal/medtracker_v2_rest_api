package com.cathalob.medtracker.payload.request.patient;

import com.cathalob.medtracker.payload.data.BloodPressureData;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddDatedBloodPressureReadingRequest {
    private LocalDate date;
    private BloodPressureData data;
}