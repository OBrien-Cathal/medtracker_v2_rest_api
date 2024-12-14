package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionScheduleEntryRepository extends JpaRepository<PrescriptionScheduleEntry,Integer> {
}
