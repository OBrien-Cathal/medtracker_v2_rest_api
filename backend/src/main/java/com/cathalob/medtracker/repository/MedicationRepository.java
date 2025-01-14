package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.prescription.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  MedicationRepository extends JpaRepository<Medication, Long> {
   public List<Medication> findByName(String name);
}
