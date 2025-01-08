package com.cathalob.medtracker.service.api.impl;

import com.cathalob.medtracker.mapper.PrescriptionMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.payload.data.PrescriptionData;
import com.cathalob.medtracker.repository.MedicationRepository;
import com.cathalob.medtracker.repository.PrescriptionScheduleEntryRepository;
import com.cathalob.medtracker.repository.PrescriptionsRepository;
import com.cathalob.medtracker.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class PrescriptionsService {
    private final UserService userService;
    private final MedicationRepository medicationRepository;
    private final PrescriptionScheduleEntryRepository prescriptionScheduleEntryRepository;
    private final PrescriptionsRepository prescriptionsRepository;

    public void saveMedications(List<Medication> medicationList) {
        medicationRepository.saveAll(medicationList);

    }

    public Map<Long, Medication> getMedicationsById() {
        return medicationRepository.findAll()
                .stream().collect(Collectors.toMap(Medication::getId, Function.identity()));
    }

    public List<PrescriptionData> getPrescriptions(String username) {
        UserModel userModel = userService.findByLogin(username);
        if (userModel == null) return List.of();

        return getPrescriptions(userModel).stream().map((PrescriptionMapper::Overview)).toList();
    }

    public List<Prescription> getPrescriptions(UserModel userModel) {
        if (userModel.getRole().equals(USERROLE.PRACTITIONER)) {
            return prescriptionsRepository.findByPractitioner(userModel);
        } else if (userModel.getRole().equals(USERROLE.PATIENT)) {
            return prescriptionsRepository.findByPatient(userModel);
        }
        return List.of();
    }

    public List<Prescription> getPrescriptions() {
        return prescriptionsRepository.findAll();
    }

    public Map<Long, Prescription> getPrescriptionsById() {
        return getPrescriptions()
                .stream().collect(Collectors.toMap(Prescription::getId, Function.identity()));
    }

    private List<PrescriptionScheduleEntry> getPrescriptionScheduleEntries() {
        return prescriptionScheduleEntryRepository.findAll();
    }

    public Map<Long, PrescriptionScheduleEntry> getPrescriptionScheduleEntriesById() {
        return prescriptionScheduleEntryRepository.findAll().stream().collect(Collectors.toMap(PrescriptionScheduleEntry::getId, Function.identity()));
    }


    public List<PrescriptionScheduleEntry> getPatientPrescriptionScheduleEntries(UserModel userModel) {
        return prescriptionScheduleEntryRepository.findAll().stream().filter(pse -> pse.getPrescription().getPatient().getId().equals(userModel.getId()))
                .distinct().toList();
    }

    public List<Prescription> getPatientPrescriptions(UserModel userModel) {
        return this.getPrescriptions().stream()
                .filter(m -> m.getPatient().getId().equals(userModel.getId())).toList();

    }

    public List<Medication> getPatientMedications(UserModel userModel) {
        return getPatientPrescriptions(userModel).stream()
                .map(Prescription::getMedication)
                .distinct()
                .toList();
    }

    public List<DAYSTAGE> getPatientPrescriptionDayStages(UserModel userModel) {
        return this.getPatientPrescriptionScheduleEntries(userModel).stream().map(PrescriptionScheduleEntry::getDayStage).distinct().toList();
    }

    public HashMap<Medication, HashSet<LocalDate>> getPatientPrescriptionDatesByMedication(UserModel userModel) {
        HashMap<Medication, HashSet<LocalDate>> medDates = new HashMap<>();
        this.getPatientPrescriptions(userModel)
                .stream()
                .collect(Collectors.groupingBy(Prescription::getMedication))
                .forEach((medication, prescriptions) ->

                        prescriptions.forEach(prescription -> {
                            LocalDate start = prescription.getBeginTime().toLocalDate();
                            LocalDate end = ((prescription.getEndTime() == null) ? LocalDate.now() : prescription.getEndTime().toLocalDate()).plusDays(1);
                            long numDays = ChronoUnit.DAYS.between(start, end);
                            List<LocalDate> dates = Stream.iterate(start, date -> date.plusDays(1)).limit(numDays).toList();
                            medDates.putIfAbsent(medication, new HashSet<>());
                            medDates.get(medication).addAll(dates);
                        })
                );

        return medDates;
    }


    public void savePrescriptions(List<Prescription> newPrescriptions) {
        prescriptionsRepository.saveAll(newPrescriptions);
    }

    public void savePrescriptionScheduleEntries(List<PrescriptionScheduleEntry> newPrescriptionScheduleEntries) {
        prescriptionScheduleEntryRepository.saveAll(newPrescriptionScheduleEntries);
    }

}
