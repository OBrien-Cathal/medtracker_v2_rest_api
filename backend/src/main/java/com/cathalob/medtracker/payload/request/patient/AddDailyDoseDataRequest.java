package com.cathalob.medtracker.payload.request.patient;

import com.cathalob.medtracker.payload.data.DailyDoseData;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDate;

@Data
@Builder
public class AddDailyDoseDataRequest {
    private LocalDate date;
    private DailyDoseData dailyDoseData;
}
