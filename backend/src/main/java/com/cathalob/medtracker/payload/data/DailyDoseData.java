package com.cathalob.medtracker.payload.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyDoseData {
    private Long doseId;
    private LocalDateTime doseTime;
    private Long prescriptionScheduleEntryId;
    private String dayStage;
    private boolean taken;
}
