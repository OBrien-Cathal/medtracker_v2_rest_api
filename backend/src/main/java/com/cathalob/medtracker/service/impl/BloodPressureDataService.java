package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.validation.bloodpressure.AddBloodPressureDailyDataException;
import com.cathalob.medtracker.exception.validation.bloodpressure.BloodPressureDailyDataExceptionData;

import com.cathalob.medtracker.exception.validation.bloodpressure.BloodPressureGraphDataException;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.repository.BloodPressureReadingRepository;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class BloodPressureDataService {
    private final BloodPressureReadingRepository bloodPressureReadingRepository;
    private final UserService userService;
    private final PatientRegistrationRepository patientRegistrationRepository;
    private final EvaluationDataService evaluationDataService;


    public void saveBloodPressureReadings(List<BloodPressureReading> bloodPressureReadings) {
        bloodPressureReadingRepository.saveAll(bloodPressureReadings);
    }

    public TreeMap<LocalDate, List<BloodPressureReading>> getBloodPressureReadingsForDateRange(@NonNull String username, LocalDate start, LocalDate end) {
        return getBloodPressureGraphData(userService.findByLogin(username), start, end, false);
    }

    public TreeMap<LocalDate, List<BloodPressureReading>> getPatientBloodPressureReadingsForDateRange(Long patientUserModelId, String practitionerUsername, LocalDate start, LocalDate end) {
        UserModel practitioner = userService.findByLogin(practitionerUsername);
//        validate that the practitioner is a doc of the patient, and allowed to see the patient data
        Optional<UserModel> maybePatient = userService.findUserModelById(patientUserModelId);
        if (maybePatient.isEmpty()) throw new BloodPressureGraphDataException(List.of("Patient does not exist"));

        if (patientRegistrationRepository.findByUserModelAndPractitionerUserModel(maybePatient.get(), practitioner).isEmpty()) {
            throw new BloodPressureGraphDataException(List.of("Only registered practitioners can view this patients data"));
        }

        return getBloodPressureGraphData(maybePatient.get(), start, end, false);
    }

    public List<BloodPressureReading> getBloodPressureData(
            String username,
            LocalDate date) throws BloodPressureDailyDataExceptionData {
        UserModel patient = userService.findByLogin(username);

        if (!patient.getRole().equals(USERROLE.PATIENT))
            throw new BloodPressureDailyDataExceptionData(List.of("User is not a patient"));

        return bloodPressureReadingRepository.findByDailyEvaluation(evaluationDataService.findForPatientAndDate(patient, date));
    }


    public Long addBloodPressureReading(BloodPressureReading newReading,
                                        LocalDate date,
                                        String username) {
        UserModel patient = userService.findByLogin(username);

        if (!patient.getRole().equals(USERROLE.PATIENT)) {
            throw new AddBloodPressureDailyDataException(List.of("User is not a patient"));
        }

        DailyEvaluation dailyEvaluation = evaluationDataService.findOrCreateDailyEvaluationForPatientAndDate(patient, date);
        newReading.setDailyEvaluation(dailyEvaluation);

        BloodPressureReading saved = bloodPressureReadingRepository.save(newReading);
        return saved.getId();

    }

    public List<BloodPressureReading> getAllBloodPressureReadings(String username) {
        return bloodPressureReadingRepository.findByUserModelId(userService.findByLogin(username).getId());
    }


    private List<BloodPressureReading> getBloodPressureReadingsForEvaluations(UserModel patient, List<DailyEvaluation> dailyEvaluations) {

        return bloodPressureReadingRepository.findByDailyEvaluationDatesAndUserModelId(
                dailyEvaluations.stream().map(DailyEvaluation::getRecordDate).toList(),
                patient.getId()
        );
    }

    private TreeMap<LocalDate, List<BloodPressureReading>> getBloodPressureGraphData(UserModel patient, LocalDate start, LocalDate end, boolean interpolate) {
        if (start == null || end == null) {
            throw new BloodPressureGraphDataException(List.of("No date range provided"));
        }

        List<DailyEvaluation> da = evaluationDataService.findDailyEvaluationsByUserModelActiveBetween(patient, start, end);
        Map<LocalDate, List<BloodPressureReading>> byDate = getBloodPressureReadingsForEvaluations(patient, da).stream()
                .collect(Collectors
                        .groupingBy(bloodPressureReading -> bloodPressureReading.getReadingTime().toLocalDate()));

        TreeMap<LocalDate, List<BloodPressureReading>> byDateForFullRange = new TreeMap<>();


        LocalDate current = start;
        while (current.isEqual(end) || current.isBefore(end)) {
            List<BloodPressureReading> forCurrent = byDate.get(current);

            byDateForFullRange.put(current, forCurrent != null ? forCurrent : new ArrayList<>());
            current = current.plusDays(1);
        }

        return byDateForFullRange;

    }


}
