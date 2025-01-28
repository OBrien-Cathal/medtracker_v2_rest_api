package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodPressureReadingRepository extends JpaRepository<BloodPressureReading, Long> {

    List<BloodPressureReading> findByDailyEvaluation(DailyEvaluation dailyEvaluation);
}
