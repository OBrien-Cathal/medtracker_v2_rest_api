package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.validation.medication.MedicationValidationException;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.payload.response.Response;
import com.cathalob.medtracker.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class MedicationsService {

    private final MedicationRepository medicationRepository;

    public List<Medication> getMedications() {
        return medicationRepository.findAll();
    }

    public Response addMedication(Medication medication) {
        try {
            validateMedication(medication);
        } catch (MedicationValidationException exception) {
            return new Response(false, "Failed", List.of(exception.getMessage()));
        }
        medicationRepository.save(medication);

        return new Response(true, "Medication added successfully, Id: " + medication.getId());
    }

    private void validateMedication(Medication medication) {
        List<Medication> existing = medicationRepository.findByName(medication.getName());
        if (!existing.isEmpty())
            throw new MedicationValidationException("Medication already exists with name: " + medication.getName());
    }
}
