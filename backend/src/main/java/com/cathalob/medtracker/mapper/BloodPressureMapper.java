package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.payload.data.BloodPressureData;


import java.time.LocalDateTime;

public class BloodPressureMapper {

    public static BloodPressureReading ToBloodPressureReading(BloodPressureData data, DailyEvaluation dailyEvaluation) {
        return BloodPressureReading.builder()
                .readingTime(LocalDateTime.now())
                .dailyEvaluation(dailyEvaluation)
                .systole(data.getSystole())
                .diastole(data.getDiastole())
                .heartRate(data.getHeartRate())
                .dayStage(data.getDayStage())
                .build();
    }
}
