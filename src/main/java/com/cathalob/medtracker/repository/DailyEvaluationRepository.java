package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.DailyEvaluationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DailyEvaluationRepository extends JpaRepository<DailyEvaluation, DailyEvaluationId> {
    @Query("FROM DAILYEVALUATION e WHERE e.userModel.id = :userModelId")
    List<DailyEvaluation> findDailyEvaluationsForUserModelId(@Param("userModelId") Long userModelId);
}
