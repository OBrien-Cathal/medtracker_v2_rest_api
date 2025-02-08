package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.mapper.BloodPressureMapper;
import com.cathalob.medtracker.mapper.DailyEvaluationMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.DailyEvaluationId;
import com.cathalob.medtracker.payload.data.BloodPressureData;
import com.cathalob.medtracker.payload.data.GraphData;
import com.cathalob.medtracker.payload.request.graph.GraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.patient.AddDatedBloodPressureReadingRequest;
import com.cathalob.medtracker.payload.request.patient.AddDatedBloodPressureReadingRequestResponse;
import com.cathalob.medtracker.payload.request.patient.DatedBloodPressureDataRequest;
import com.cathalob.medtracker.payload.response.BloodPressureDataRequestResponse;
import com.cathalob.medtracker.payload.response.TimeSeriesGraphDataResponse;
import com.cathalob.medtracker.repository.BloodPressureReadingRepository;
import com.cathalob.medtracker.repository.DailyEvaluationRepository;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class BloodPressureDataService {
    private final BloodPressureReadingRepository bloodPressureReadingRepository;
    private final UserService userService;
    private final PatientRegistrationRepository patientRegistrationRepository;
    private final DailyEvaluationRepository dailyEvaluationRepository;

    public void saveBloodPressureReadings(List<BloodPressureReading> bloodPressureReadings) {
        bloodPressureReadingRepository.saveAll(bloodPressureReadings);
    }

    public TimeSeriesGraphDataResponse getSystoleGraphData(@NonNull String username, GraphDataForDateRangeRequest request) {
        System.out.println(request);
        return getBloodPressureGraphData(BloodPressureReading::getSystole, userService.findByLogin(username), request.getStart(), request.getEnd(), false);
    }

    public TimeSeriesGraphDataResponse getPatientSystoleGraphData(Long patientUserModelId, String practitionerUsername, GraphDataForDateRangeRequest request) {
        UserModel practitioner = userService.findByLogin(practitionerUsername);
//        validate that the practitioner is a doc of the patient, and allowed to see the patient data
        Optional<UserModel> maybePatient = userService.findUserModelById(patientUserModelId);
        if (maybePatient.isEmpty()) return TimeSeriesGraphDataResponse.Failure();
        if (patientRegistrationRepository.findByUserModelAndPractitionerUserModel(maybePatient.get(), practitioner).isEmpty()) {
            return TimeSeriesGraphDataResponse.Failure(List.of("Only registered practitioners can view this patients data"));

        }
        return maybePatient.map(userModel ->
                getBloodPressureGraphData(BloodPressureReading::getSystole, userModel, request.getStart(), request.getEnd(), false)
        ).orElseGet(TimeSeriesGraphDataResponse::Failure);
    }

    public BloodPressureDataRequestResponse getBloodPressureData(
            DatedBloodPressureDataRequest datedBloodPressureDataRequest,
            String username) {
        UserModel patient = userService.findByLogin(username);

        if (!patient.getRole().equals(USERROLE.PATIENT))
            return BloodPressureDataRequestResponse.Failed(List.of("User is not a patient"));
        Optional<DailyEvaluation> dailyEvaluationOptional = dailyEvaluationRepository.findById(new DailyEvaluationId(patient.getId(), datedBloodPressureDataRequest.getDate()));
        if (dailyEvaluationOptional.isEmpty()) {
//            return BloodPressureDataRequestResponse.Failed(List.of("No daily evaluation found for this patient and date"));
            return BloodPressureDataRequestResponse.Success(List.of());
        }

        List<BloodPressureReading> readings = bloodPressureReadingRepository.findByDailyEvaluation(dailyEvaluationOptional.get());

        List<BloodPressureData> data = readings.stream().map((r) -> BloodPressureData.builder()
                .readingTime(r.getReadingTime())
                .systole(r.getSystole())
                .diastole(r.getDiastole())
                .heartRate(r.getHeartRate())
                .dayStage(r.getDayStage())
                .build()).toList();
        return BloodPressureDataRequestResponse.Success(data);
    }

    public AddDatedBloodPressureReadingRequestResponse addBloodPressureReading(
            AddDatedBloodPressureReadingRequest addRequest, String username) {
        UserModel patient = userService.findByLogin(username);

        if (!patient.getRole().equals(USERROLE.PATIENT))
            return AddDatedBloodPressureReadingRequestResponse.Failed(List.of("User is not a patient"));
        DailyEvaluation newDailyEvaluation = DailyEvaluationMapper.ToDailyEvaluation(
                addRequest.getDate(), patient);
        Optional<DailyEvaluation> existingDailyEvaluationOptional = dailyEvaluationRepository.findById(newDailyEvaluation.getDailyEvaluationIdClass());
        if (existingDailyEvaluationOptional.isEmpty()) {
            dailyEvaluationRepository.save(newDailyEvaluation);
        }

        BloodPressureReading newReading = BloodPressureMapper.ToBloodPressureReading(addRequest.getData(), newDailyEvaluation);
        System.out.println(newReading);
        BloodPressureReading saved = bloodPressureReadingRepository.save(newReading);
        System.out.println(saved);
        return AddDatedBloodPressureReadingRequestResponse.Success(saved.getId());
    }

    public TimeSeriesGraphDataResponse getDiastoleGraphData(String username) {
        return getDiastoleGraphData(userService.findByLogin(username));
    }

    public TimeSeriesGraphDataResponse getHeartRateGraphData(String username) {
        return getHeartRateGraphData(userService.findByLogin(username));
    }

    private TimeSeriesGraphDataResponse getSystoleGraphData(UserModel userModel) {
        return getBloodPressureGraphData(BloodPressureReading::getSystole, userModel, null, null, false);
    }

    private List<BloodPressureReading> getBloodPressureReadingsForEvaluations(List<DailyEvaluation> dailyEvaluations) {
        List<BloodPressureReading> list = dailyEvaluations
                .stream()
                .map(bloodPressureReadingRepository::findByDailyEvaluation)
                .flatMap(List::stream)
                .toList();
        return list;
    }

    private TimeSeriesGraphDataResponse getDiastoleGraphData(UserModel userModel) {
        return getBloodPressureGraphData(BloodPressureReading::getDiastole, userModel, null, null, false);
    }

    private TimeSeriesGraphDataResponse getHeartRateGraphData(UserModel userModel) {
        return getBloodPressureGraphData(BloodPressureReading::getHeartRate, userModel, null, null, false);
    }

    private TimeSeriesGraphDataResponse getBloodPressureGraphData(ToIntFunction<BloodPressureReading> bpDataAccessorFunction, UserModel userModel, LocalDate start, LocalDate end, boolean interpolate) {
        if (start == null || end == null) {
            return TimeSeriesGraphDataResponse.Failure(List.of("No date range provided"));
        }

        List<DailyEvaluation> da = dailyEvaluationRepository.findDailyEvaluationsByUserModel(userModel)
                .stream()
                .filter(dailyEvaluation ->
                {
                    boolean between = (dailyEvaluation.getRecordDate().isEqual(start) || dailyEvaluation.getRecordDate().isAfter(start))
                            &&
                            (dailyEvaluation.getRecordDate().isEqual(end) || dailyEvaluation.getRecordDate().isBefore(end));
//                    if (between) {
//                        System.out.println("Between " + dailyEvaluation.getRecordDate());
//                    } else {
//                        System.out.println("NOt between " + dailyEvaluation.getRecordDate());
//                    }
                    return between;

                }).toList();

        List<BloodPressureReading> bloodPressureReadings = getBloodPressureReadingsForEvaluations(
                da);

        return TimeSeriesGraphDataResponse.Success(
                new GraphData(
                        getSortedDataColumnNames(bloodPressureReadings),
                        getBloodPressureGraphData(bpDataAccessorFunction, bloodPressureReadings)));
    }


    private List<String> getSortedDataColumnNames(List<BloodPressureReading> bloodPressureReadings) {
        List<DAYSTAGE> daystageList = bloodPressureReadings.stream().map(BloodPressureReading::getDayStage)
                .distinct()
                .sorted(Comparator.comparing(DAYSTAGE::ordinal))
                .toList();
        List<String> strings = new ArrayList<>();
        strings.add("Date");
        strings.addAll(daystageList.stream().map(ds -> (
                ds.toString().charAt(0) + ds.toString().substring(1).toLowerCase())).toList());
        return strings;
    }

    private List<List<Object>> getBloodPressureGraphData(ToIntFunction<BloodPressureReading> getBpValueFunction, List<BloodPressureReading> bloodPressureReadings) {
        List<List<Object>> listData = new ArrayList<>();

        TreeMap<LocalDate, List<BloodPressureReading>> byDate = bloodPressureReadings.stream().sorted(Comparator.comparing(bloodPressureReading -> bloodPressureReading.getReadingTime().toLocalDate()))
                .collect(Collectors.groupingBy(bloodPressureReading -> bloodPressureReading.getReadingTime().toLocalDate(), TreeMap::new, Collectors.toList()));

        List<DAYSTAGE> sortedDayStages = bloodPressureReadings.stream().map(BloodPressureReading::getDayStage).distinct().sorted(Comparator.comparing(DAYSTAGE::ordinal)).toList();

        byDate.forEach((date, bloodPressureReadingsByDate) -> {
            ArrayList<Object> dayList = new ArrayList<>();
            dayList.add(date);

            Map<DAYSTAGE, List<BloodPressureReading>> bprMap = bloodPressureReadingsByDate.stream().collect(Collectors.groupingBy(BloodPressureReading::getDayStage));

            for (DAYSTAGE dayStage : sortedDayStages) {
                if (bprMap.containsKey(dayStage)) {
                    OptionalDouble average = bprMap.get(dayStage).stream().mapToInt(getBpValueFunction).average();
                    dayList.add(average.isEmpty() ? null : ((int) average.getAsDouble()));
                } else {
                    dayList.add(null);
                }
            }
            listData.add(dayList);
        });
        return listData;
    }
}
