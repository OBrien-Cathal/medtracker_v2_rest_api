package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.prescription.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  PrescriptionsRepository extends JpaRepository<Prescription,Long> {

    List<Prescription> findByPractitioner(UserModel practitioner);
    List<Prescription> findByPatient(UserModel patient);
}
