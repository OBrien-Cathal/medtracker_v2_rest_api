package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.mapper.PrescriptionMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.payload.data.PrescriptionDetailsData;
import com.cathalob.medtracker.payload.data.PrescriptionOverviewData;
import com.cathalob.medtracker.payload.response.SubmitPrescriptionDetailsResponse;
import com.cathalob.medtracker.payload.response.GetPrescriptionDetailsResponse;
import com.cathalob.medtracker.repository.MedicationRepository;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.repository.PrescriptionScheduleEntryRepository;
import com.cathalob.medtracker.repository.PrescriptionsRepository;
import com.cathalob.medtracker.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
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
    private final PatientRegistrationRepository patientRegistrationRepository;


    //    Used by controller!!
    public List<PrescriptionOverviewData> getPrescriptions(String username) {
        UserModel userModel = userService.findByLogin(username);
        if (userModel == null) return List.of();
        return getPrescriptions(userModel).stream().map((PrescriptionMapper::Overview)).toList();
    }

    public List<PrescriptionOverviewData> getPatientPrescriptions(String practitionerUsername, Long patientId) {
        Optional<UserModel> maybePatient = userService.findUserModelById(patientId);
        if (maybePatient.isEmpty()) return List.of();
        UserModel practitioner = userService.findByLogin(practitionerUsername);
        if (patientRegistrationRepository.findByUserModelAndPractitionerUserModel(maybePatient.get(), practitioner).isEmpty()) {
            return List.of();
        }
//        check if the pract can see these prescriptions, send error that user does not exist instead of empty list, subclass response
        return getPrescriptions(maybePatient.get()).stream().map((PrescriptionMapper::Overview)).toList();
    }

    public GetPrescriptionDetailsResponse getPrescriptionDetails(String practitionerUsername, Long prescriptionId) {
        System.out.println("getPrescriptionDetails");
        Prescription prescriptionOrNull = prescriptionsRepository.findById(prescriptionId).orElse(null);
        if (prescriptionOrNull == null)
            return GetPrescriptionDetailsResponse.Failed(List.of("Prescription with ID does not exist (ID: " + prescriptionId + ")"));

        UserModel practitionerOrPatient = userService.findByLogin(practitionerUsername);
        if (practitionerOrPatient.getRole().equals(USERROLE.PRACTITIONER)) {


            UserModel patient = prescriptionOrNull.getPatient();
            if (patientRegistrationRepository.findByUserModelAndPractitionerUserModel(patient, practitionerOrPatient).isEmpty()) {
                return GetPrescriptionDetailsResponse.Failed(List.of("Not allowed to view prescriptions of unregistered patient"));
            }

            return GetPrescriptionDetailsResponse.Success(
                    PrescriptionMapper.PrescriptionDetails(
                            prescriptionOrNull,
                            prescriptionScheduleEntryRepository.findByPrescription(prescriptionOrNull)));
        } else {
            if (practitionerOrPatient.getId().equals(prescriptionOrNull.getPatient().getId())) {
                return GetPrescriptionDetailsResponse.Success(
                        PrescriptionMapper.PrescriptionDetails(
                                prescriptionOrNull,
                                prescriptionScheduleEntryRepository.findByPrescription(prescriptionOrNull)));
            }
        }

        return GetPrescriptionDetailsResponse.Failed(List.of("Not authorized"));
    }

    public SubmitPrescriptionDetailsResponse addPrescriptionDetails(PrescriptionDetailsData prescriptionDetailsData) {
        System.out.println("addPrescriptionDetails");
        System.out.println(prescriptionDetailsData);
        Prescription prescription = PrescriptionMapper.Prescription(prescriptionDetailsData);
        setReferencesFromPrescriptionDetails(prescriptionDetailsData, prescription);
        System.out.println(prescription);

        List<String> errors = validateAddPrescription(prescription);
        if (!errors.isEmpty()) {
            return SubmitPrescriptionDetailsResponse.Failed(errors);
        }


        Prescription saved = prescriptionsRepository.save(prescription);
        updatePrescriptionSchedule(prescriptionDetailsData, saved);
        return SubmitPrescriptionDetailsResponse.Success(saved.getId());
    }

    private void updatePrescriptionSchedule(PrescriptionDetailsData prescriptionDetailsData,
                                            Prescription prescription) {
        List<PrescriptionScheduleEntry> toAdd = new ArrayList<>();
        List<PrescriptionScheduleEntry> toRemove = new ArrayList<>();

        List<PrescriptionScheduleEntry> existingSchedule = prescriptionScheduleEntryRepository.findByPrescription(prescription);

        Map<DAYSTAGE, PrescriptionScheduleEntry> existingByDayStage = existingSchedule
                .stream()
                .collect(Collectors.toMap(PrescriptionScheduleEntry::getDayStage, Function.identity()));
        for (PrescriptionScheduleEntry prescriptionScheduleEntry : prescriptionDetailsData.getPrescriptionScheduleEntries()) {
            if (!existingByDayStage.containsKey(prescriptionScheduleEntry.getDayStage())) {
                toAdd.add(prescriptionScheduleEntry);
            }
        }

        Map<DAYSTAGE, PrescriptionScheduleEntry> newByDayStage = prescriptionDetailsData.getPrescriptionScheduleEntries().stream()
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

    private List<String> validateAddPrescription(Prescription prescription) {
        ArrayList<String> errors = new ArrayList<>();
        if ((prescription.getId() != null)) {
            errors.add("Added prescriptions cannot have an ID, provided: " + prescription.getId());
        }
        errors.addAll(basicValidatePrescription(prescription));
        return errors;
    }

    public SubmitPrescriptionDetailsResponse updatePrescriptionDetails(PrescriptionDetailsData prescriptionDetailsData) {
        System.out.println("updatePrescriptionDetails");
        System.out.println(prescriptionDetailsData);
        Prescription prescription = PrescriptionMapper.Prescription(prescriptionDetailsData);
        setReferencesFromPrescriptionDetails(prescriptionDetailsData, prescription);
        System.out.println(prescription);
        Prescription existingPrescriptionOrNull = prescriptionsRepository.findById(prescription.getId()).orElse(null);
        List<String> errors = validateUpdatePrescription(prescription, existingPrescriptionOrNull);
        if (!errors.isEmpty()) {
            return SubmitPrescriptionDetailsResponse.Failed(errors);
        }

        Prescription saved = prescriptionsRepository.save(prescription);
        updatePrescriptionSchedule(prescriptionDetailsData, saved);

        return SubmitPrescriptionDetailsResponse.Success(saved.getId());
    }

    private void setReferencesFromPrescriptionDetails(PrescriptionDetailsData prescriptionDetailsData, Prescription prescription) {
        prescription.setPatient(userService.findUserModelById(prescriptionDetailsData.getPatientId()).orElse(null));
        prescription.setPractitioner(userService.findUserModelById(prescriptionDetailsData.getPractitionerId()).orElse(null));
        prescription.setMedication(medicationRepository.findById(prescriptionDetailsData.getMedication().getId()).orElse(null));
    }

    private List<String> validateUpdatePrescription(Prescription prescription, Prescription existingPrescription) {
        ArrayList<String> errors = new ArrayList<>();
        if ((prescription.getId() == null)) {
            errors.add("No ID provided to update");
        }
        if (existingPrescription == null) {
            errors.add("Prescription with ID does not exist (ID: " + prescription.getId() + ")");
        }
        errors.addAll(basicValidatePrescription(prescription));
        return errors;
    }

    private List<String> basicValidatePrescription(Prescription prescription) {
        ArrayList<String> errors = new ArrayList<>();

        if (prescription.getDoseMg() < 0) {
            errors.add("Dose mg should be greater than 0");
        }
        return errors;
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

    private List<PrescriptionScheduleEntry> getPrescriptionScheduleEntries(List<Long> prescriptionIds) {
        return prescriptionScheduleEntryRepository.findByPrescriptionIds(prescriptionIds);
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
