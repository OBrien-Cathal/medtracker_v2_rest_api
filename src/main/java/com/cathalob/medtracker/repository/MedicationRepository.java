package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.prescription.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  MedicationRepository extends JpaRepository<Medication, Integer> {
}
