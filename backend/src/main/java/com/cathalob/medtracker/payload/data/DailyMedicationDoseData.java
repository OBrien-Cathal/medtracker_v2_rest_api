package com.cathalob.medtracker.payload.data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyMedicationDoseData {
    private String medicationName;
    private Integer doseMg;
    private Long prescriptionId;
    private List<DailyDoseData> doses;

}
