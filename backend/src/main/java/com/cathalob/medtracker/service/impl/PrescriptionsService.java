package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.validation.PrescriptionValidatorException;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;

import com.cathalob.medtracker.puremodel.PrescriptionDetails;
import com.cathalob.medtracker.repository.MedicationRepository;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.repository.PrescriptionScheduleEntryRepository;
import com.cathalob.medtracker.repository.PrescriptionsRepository;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.validate.actions.GetPrescriptionDetailsValidator;
import com.cathalob.medtracker.validate.model.PrescriptionValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class PrescriptionsService {
    private final UserService userService;
    private final MedicationRepository medicationRepository;
    private final PrescriptionScheduleEntryRepository prescriptionScheduleEntryRepository;
    private final PrescriptionsRepository prescriptionsRepository;
    private final PatientRegistrationRepository patientRegistrationRepository;


    //    Used by controller!!
    public List<Prescription> getPrescriptions(String username) {
        UserModel userModel = userService.findByLogin(username);
        if (userModel == null) return List.of();
        return getPrescriptions(userModel);
    }

    public List<Prescription> getPatientPrescriptions(String practitionerUsername, Long patientId) {
        Optional<UserModel> maybePatient = userService.findUserModelById(patientId);
        if (maybePatient.isEmpty()) return List.of();
        UserModel practitioner = userService.findByLogin(practitionerUsername);
        if (patientRegistrationRepository.findByUserModelAndPractitionerUserModel(maybePatient.get(), practitioner).isEmpty()) {
            return List.of();
        }
//        check if the pract can see these prescriptions, send error that user does not exist instead of empty list, subclass response
        return getPrescriptions(maybePatient.get());
    }

    public PrescriptionDetails getPrescriptionDetails(String practitionerUsername, Long prescriptionId) {
        Prescription prescriptionOrNull = prescriptionsRepository.findById(prescriptionId).orElse(null);
        UserModel requestingUserModel = userService.findByLogin(practitionerUsername);

        UserModel patient = prescriptionOrNull == null ? null : prescriptionOrNull.getPatient();
        List<PatientRegistration> patientRegistrations = patientRegistrationRepository.findByUserModelAndPractitionerUserModel(patient, requestingUserModel);

        new GetPrescriptionDetailsValidator(prescriptionOrNull, requestingUserModel, patientRegistrations).validate();

        return new PrescriptionDetails(prescriptionOrNull, prescriptionScheduleEntryRepository.findByPrescription(prescriptionOrNull));
    }


    public Long submitPrescription(String practitionerUsername,
                                   Prescription prescription,
                                   List<PrescriptionScheduleEntry> prescriptionScheduleEntryList,
                                   Long patientId,
                                   Long medicationId) {

        Prescription existingPrescription = prescription.getId() == null ? null :
                prescriptionsRepository.findById(prescription.getId())
                        .orElseThrow(() -> new PrescriptionValidatorException(
                                List.of("Prescription with ID does not exist (ID: " + prescription.getId() + ")")));

        prescription.setPatient(userService.findUserModelById(patientId).orElse(null));
        UserModel submittingUser = userService.findByLogin(practitionerUsername);

        List<PatientRegistration> registration = patientRegistrationRepository.findByUserModelAndPractitionerUserModel(prescription.getPatient(), submittingUser);
        UserModel registeredPractitioner = registration.isEmpty() ? null : registration.get(0).getPractitionerUserModel();
        prescription.setPractitioner(registeredPractitioner);
        prescription.setMedication(medicationRepository.findById(medicationId).orElse(null));

        PrescriptionValidator.aPrescriptionValidator(prescription, existingPrescription).validate();

        Prescription saved = prescriptionsRepository.save(prescription);
        updatePrescriptionSchedule(prescriptionScheduleEntryList, saved);

        return saved.getId();
    }


    private void updatePrescriptionSchedule(List<PrescriptionScheduleEntry> prescriptionScheduleEntryList,
                                            Prescription prescription) {
        List<PrescriptionScheduleEntry> toAdd = new ArrayList<>();
        List<PrescriptionScheduleEntry> toRemove = new ArrayList<>();

        List<PrescriptionScheduleEntry> existingSchedule = prescriptionScheduleEntryRepository.findByPrescription(prescription);

        Map<DAYSTAGE, PrescriptionScheduleEntry> existingByDayStage = existingSchedule
                .stream()
                .collect(Collectors.toMap(PrescriptionScheduleEntry::getDayStage, Function.identity()));
        for (PrescriptionScheduleEntry prescriptionScheduleEntry : prescriptionScheduleEntryList) {
            if (!existingByDayStage.containsKey(prescriptionScheduleEntry.getDayStage())) {
                toAdd.add(prescriptionScheduleEntry);
            }
        }

        Map<DAYSTAGE, PrescriptionScheduleEntry> newByDayStage = prescriptionScheduleEntryList.stream()
                .collect(Collectors.toMap(PrescriptionScheduleEntry::getDayStage, Function.identity()));

        for (PrescriptionScheduleEntry existingScheduleEntry : existingSchedule) {
            if (!newByDayStage.containsKey(existingScheduleEntry.getDayStage())) {
                toRemove.add(existingScheduleEntry);
            }
        }
        toAdd.forEach(newEntry -> newEntry.setPrescription(prescription));
        prescriptionScheduleEntryRepository.deleteAll(toRemove);
        prescriptionScheduleEntryRepository.saveAll(toAdd);

    }

    //    Internal use
    public void saveMedications(List<Medication> medicationList) {
        medicationRepository.saveAll(medicationList);
    }

    public Map<Long, Medication> getMedicationsById() {
        return medicationRepository.findAll()
                .stream().collect(Collectors.toMap(Medication::getId, Function.identity()));
    }

    public List<Prescription> getPrescriptions(UserModel userModel) {
        return prescriptionsRepository.findByPatient(userModel);
    }

    public List<Prescription> getPrescriptionsValidOnDate(UserModel patient, LocalDate date) {

        return getPrescriptions(patient)
                .stream()
                .filter(prescription -> {
                    ChronoLocalDate begin = ChronoLocalDate.from(prescription.getBeginTime());

                    boolean endTimeValid = prescription.getEndTime() == null;
                    if (!endTimeValid) {
                        ChronoLocalDate end = ChronoLocalDate.from(prescription.getEndTime());
                        endTimeValid = (date.isBefore(end) || date.equals(end));
                    }
                    return (date.isAfter(begin) || date.equals(begin)) &&
                            endTimeValid;
                }).toList();
    }

    public HashMap<LocalDate, List<PrescriptionScheduleEntry>> getPrescriptionScheduleEntriesValidBetween(UserModel patient, LocalDate start, LocalDate end) {
        HashMap<LocalDate, List<PrescriptionScheduleEntry>> entriesByDate = new HashMap<>();
        LocalDate current = start;

        while (current.isEqual(end) || current.isBefore(end)) {

            entriesByDate.put(current, prescriptionScheduleEntryRepository
                    .findByPrescriptionIds(getPrescriptionsValidOnDate(patient, current)
                            .stream()
                            .map(Prescription::getId)
                            .toList()));
            current = current.plusDays(1);
        }

        return entriesByDate;
    }


    private List<Prescription> getAllPrescriptions() {
        return prescriptionsRepository.findAll();
    }

    public Map<Long, Prescription> getPrescriptionsById() {
        return getAllPrescriptions()
                .stream().collect(Collectors.toMap(Prescription::getId, Function.identity()));
    }

    public Map<Long, PrescriptionScheduleEntry> getPrescriptionScheduleEntriesById() {
        return prescriptionScheduleEntryRepository.findAll().stream().collect(Collectors.toMap(PrescriptionScheduleEntry::getId, Function.identity()));
    }

    public void savePrescriptions(List<Prescription> newPrescriptions) {
        prescriptionsRepository.saveAll(newPrescriptions);
    }

    public void savePrescriptionScheduleEntries(List<PrescriptionScheduleEntry> newPrescriptionScheduleEntries) {
        prescriptionScheduleEntryRepository.saveAll(newPrescriptionScheduleEntries);
    }

}
