package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;

import java.time.LocalDateTime;

public class BloodPressureReadingBuilder {
    private Long id;
    private DailyEvaluationBuilder dailyEvaluationBuilder = new DailyEvaluationBuilder();

    private LocalDateTime readingTime = LocalDateTime.now();
    private Integer systole = 120;
    private Integer diastole = 80;
    private Integer heartRate = 60;

    public BloodPressureReadingBuilder withDaystage(DAYSTAGE daystage) {
        this.daystage = daystage;
        return this;
    }

    public BloodPressureReadingBuilder withHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
        return this;
    }

    public BloodPressureReadingBuilder withDiastole(Integer diastole) {
        this.diastole = diastole;
        return this;
    }

    public BloodPressureReadingBuilder withSystole(Integer systole) {
        this.systole = systole;
        return this;
    }

    public BloodPressureReadingBuilder withReadingTime(LocalDateTime readingTime) {
        this.readingTime = readingTime;
        return this;
    }
    public BloodPressureReadingBuilder withReadingTimeAndEvaluationDate(LocalDateTime readingTime) {
        this.dailyEvaluationBuilder.withRecordDate(readingTime.toLocalDate());
        this.readingTime = readingTime;
        return this;
    }

    public BloodPressureReadingBuilder withDailyEvaluationBuilder(DailyEvaluationBuilder dailyEvaluationBuilder) {
        this.dailyEvaluationBuilder = dailyEvaluationBuilder;
        return this;
    }

    public BloodPressureReadingBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    private DAYSTAGE daystage = DAYSTAGE.BEDTIME;


    public BloodPressureReadingBuilder() {
    }

    public static BloodPressureReadingBuilder aBloodPressureReading() {
        return new BloodPressureReadingBuilder();
    }


    public BloodPressureReading build() {
        DailyEvaluation dailyEvaluation = dailyEvaluationBuilder.build();
        return new BloodPressureReading(id, daystage, dailyEvaluation, readingTime, systole, diastole, heartRate);

    }

}
