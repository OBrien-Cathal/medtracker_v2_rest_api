package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.prescription.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  PrescriptionsRepository extends JpaRepository<Prescription,Integer> {
}
