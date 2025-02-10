package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.validation.dose.DailyDoseDataException;
import com.cathalob.medtracker.exception.validation.dose.DoseGraphDataException;
import com.cathalob.medtracker.factory.DoseServiceModelFactory;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.repository.DoseRepository;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.repository.PrescriptionScheduleEntryRepository;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.validate.model.DoseValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class DoseService {
    private final PrescriptionsService prescriptionsService;
    private final DoseRepository doseRepository;
    private final UserService userService;
    private final PatientRegistrationRepository patientRegistrationRepository;
    private final EvaluationDataService evaluationDataService;
    private final PrescriptionScheduleEntryRepository prescriptionScheduleEntryRepository;
    private final DoseServiceModelFactory factory;


    public void saveDoses(List<Dose> newDoses) {
        doseRepository.saveAll(newDoses);
    }

    public Map<Long, Dose> getDosesById() {
        return doseRepository.findAll().stream().collect(Collectors.toMap(Dose::getId, Function.identity()));
    }

    public TreeMap<LocalDate, List<Dose>> getDoseGraphData(
            String patientUsername,
            LocalDate start, LocalDate end) throws DoseGraphDataException {

        return getFullDoseSchedule(userService.findByLogin(patientUsername), start, end, false);
    }

    public TreeMap<LocalDate, List<Dose>> getPatientDoseGraphData(
            Long patientUserModelId,
            String practitionerUsername,
            LocalDate start,
            LocalDate end) throws DoseGraphDataException {

        UserModel practitioner = userService.findByLogin(practitionerUsername);
        Optional<UserModel> maybePatient = userService.findUserModelById(patientUserModelId);
        if (maybePatient.isEmpty()) throw new DoseGraphDataException(List.of("No patient found in request"));
        UserModel patient = maybePatient.get();
//        validate that the practitioner is a doc of the patient, and allowed to see the patient data
        if (patientRegistrationRepository.findByUserModelAndPractitionerUserModel(patient, practitioner).isEmpty()) {
            throw new DoseGraphDataException(List.of("Only registered practitioners can view this patients data"));
        }

        return getFullDoseSchedule(patient, start, end, false);
    }

    public List<Dose> getDailyDoseData(String username, LocalDate date) {
        UserModel patient = userService.findByLogin(username);

        return getFullDoseSchedule(patient, date, date, false).get(date);
    }

    public Long addDailyDoseData(String username, Dose newDose, Long pseId, LocalDate date) throws DailyDoseDataException {
        UserModel patient = userService.findByLogin(username);

        DailyEvaluation dailyEvaluation = evaluationDataService.findOrCreateDailyEvaluationForPatientAndDate(patient, date);
        PrescriptionScheduleEntry prescriptionScheduleEntry = prescriptionScheduleEntryRepository.findById(pseId).orElse(null);
        List<Dose> byId = doseRepository.findByPrescriptionScheduleEntryAndEvaluation(prescriptionScheduleEntry, dailyEvaluation);
        newDose.setPrescriptionScheduleEntry(prescriptionScheduleEntry);
        newDose.setEvaluation(dailyEvaluation);

        if (byId.isEmpty()) {
            DoseValidator.AddDoseValidator(newDose).validate();
        } else {
            Dose foundDose = byId.get(0);
            newDose.setId(foundDose.getId());
            DoseValidator.UpdateDoseValidator(newDose, foundDose).validate();
        }
        Dose saved = doseRepository.save(newDose);

        return saved.getId();
    }

    private Map<LocalDate, List<Dose>> existingDoses(UserModel patient, LocalDate start, LocalDate end) {
        List<DailyEvaluation> existingDailyEvaluations = evaluationDataService.findDailyEvaluationsByUserModelActiveBetween(patient, start, end);

        return doseRepository.findByDailyEvaluationDatesAndUserModelId(
                        existingDailyEvaluations.stream().map(DailyEvaluation::getRecordDate).toList(),
                        patient.getId()).stream()
                .collect(Collectors
                        .groupingBy(dose -> dose.getEvaluation().getRecordDate()));


    }

    private TreeMap<LocalDate, List<Dose>> getFullDoseSchedule(UserModel patient, LocalDate start, LocalDate end, boolean interpolate) {

        if (start == null || end == null) {
            throw new DoseGraphDataException(List.of("No date range provided"));
        }

        Map<LocalDate, List<Dose>> existingDosesByDate = existingDoses(patient, start, end);
        Map<LocalDate, List<Dose>> dummyDosesByDate =
                factory.dummyDosesForRange(prescriptionsService.getPrescriptionScheduleEntriesValidBetween(patient, start, end));

        TreeMap<LocalDate, List<Dose>> orderedDosesMap = new TreeMap<>();

        LocalDate current = start;
        while (current.isEqual(end) || current.isBefore(end)) {

            Map<Long, Dose> existingDosesByEntryId = existingDosesByDate.getOrDefault(current, new ArrayList<>()).stream()
                    .collect(Collectors.toMap(dose -> dose.getPrescriptionScheduleEntry().getId(), Function.identity()));

            List<Dose> dummyDosesForCurrentDate = dummyDosesByDate.get(current);

            if (existingDosesByEntryId.isEmpty()) {
                //  add the full set of dummy doses for the date
                orderedDosesMap.put(current, dummyDosesForCurrentDate);
            } else {
                // if some doses exist for a date, we need to ensure that the set is complete
                orderedDosesMap.put(current, dummyDosesForCurrentDate.stream().map(dummyDose -> {
                    Dose foundExistingDoseForEntry = existingDosesByEntryId.get(dummyDose.getPrescriptionScheduleEntry().getId());
                    return foundExistingDoseForEntry == null ? dummyDose : foundExistingDoseForEntry;
                }).toList());
            }
            //  move to the next date
            current = current.plusDays(1);
        }

        return orderedDosesMap;
    }


}
