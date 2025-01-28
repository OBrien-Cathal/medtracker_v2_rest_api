package com.cathalob.medtracker.payload.data;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloodPressureData {
    private LocalDateTime readingTime;
    private Integer systole;
    private Integer diastole;
    private Integer heartRate;
    private DAYSTAGE dayStage;
}
