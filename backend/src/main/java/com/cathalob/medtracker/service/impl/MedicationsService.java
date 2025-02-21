package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.validation.medication.MedicationValidationException;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.repository.MedicationRepository;
import com.cathalob.medtracker.validate.model.MedicationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class MedicationsService {

    private final MedicationRepository medicationRepository;

    //    Controller ----------------------------
    public List<Medication> getMedications() {
        return medicationRepository.findAll();
    }

    public Long addMedication(Medication medication) throws MedicationValidationException {
        validateMedication(medication);
        Medication saved = medicationRepository.save(medication);
        return saved.getId();

    }

//    Internal ----------------------------

    private void validateMedication(Medication medication) {
        List<Medication> existing = medicationRepository.findByName(medication.getName());

        MedicationValidator.AMedicationValidator(medication, existing.isEmpty() ? null : existing.get(0)).validate();
    }

    public void addMedications(String username, List<Medication> newMedications) {
        newMedications.forEach(this::addMedication);
    }


    public Map<Long, Medication> getMedicationsById() {
        return medicationRepository.findAll()
                .stream().collect(Collectors.toMap(Medication::getId, Function.identity()));
    }
}
