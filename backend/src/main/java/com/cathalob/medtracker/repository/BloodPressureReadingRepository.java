package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BloodPressureReadingRepository extends JpaRepository<BloodPressureReading, Long> {

    List<BloodPressureReading> findByDailyEvaluation(DailyEvaluation dailyEvaluation);

    @Query( "select o from BLOODPRESSUREREADING o where o.dailyEvaluation.recordDate in :dates AND o.dailyEvaluation.userModel.id in :usermodelids" )
    List<BloodPressureReading> findByDailyEvaluationDatesAndIds(List<LocalDate> dates, List<Long> usermodelids);


}
