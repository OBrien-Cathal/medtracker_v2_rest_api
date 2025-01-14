package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.tracking.Dose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoseRepository extends JpaRepository<Dose, Long> {
    @Query("FROM DOSE e WHERE e.evaluation.userModel.id = :userModelId")
    List<Dose> findDosesForUserId(@Param("userModelId") Long userModelId);
}
