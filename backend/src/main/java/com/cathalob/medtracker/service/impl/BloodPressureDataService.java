package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.payload.data.GraphData;
import com.cathalob.medtracker.payload.response.TimeSeriesGraphDataResponse;
import com.cathalob.medtracker.repository.BloodPressureReadingRepository;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.service.UserService;
import lombok.AllArgsConstructor;
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

    public void saveBloodPressureReadings(List<BloodPressureReading> bloodPressureReadings) {
        bloodPressureReadingRepository.saveAll(bloodPressureReadings);
    }

    public List<BloodPressureReading> getBloodPressureReadings(UserModel userModel) {
        return bloodPressureReadingRepository.findAll().stream()
                .filter(bloodPressureReading -> bloodPressureReading.getDailyEvaluation().getUserModel().getId().equals(userModel.getId()))
                .toList();
    }

    public TimeSeriesGraphDataResponse getSystoleGraphData(String username) {
        return getSystoleGraphData(userService.findByLogin(username));
    }

    public TimeSeriesGraphDataResponse getPatientSystoleGraphData(Long patientUserModelId, String practitionerUsername) {
        UserModel practitioner = userService.findByLogin(practitionerUsername);
//        validate that the practitioner is a doc of the patient, and allowed to see the patient data
        Optional<UserModel> maybePatient = userService.findUserModelById(patientUserModelId);
        if (maybePatient.isEmpty()) return TimeSeriesGraphDataResponse.Failure();
        if (patientRegistrationRepository.findByUserModelAndPractitionerUserModel(maybePatient.get(), practitioner).isEmpty()) {
            return TimeSeriesGraphDataResponse.Failure(List.of("Only registered practitioners can view this patients data"));

        }
        return maybePatient.map(this::getSystoleGraphData).orElseGet(TimeSeriesGraphDataResponse::Failure);
    }

    public TimeSeriesGraphDataResponse getDiastoleGraphData(String username) {
        return getDiastoleGraphData(userService.findByLogin(username));
    }

    public TimeSeriesGraphDataResponse getHeartRateGraphData(String username) {
        return getHeartRateGraphData(userService.findByLogin(username));
    }

    private TimeSeriesGraphDataResponse getSystoleGraphData(UserModel userModel) {
        return getBloodPressureGraphData(BloodPressureReading::getSystole, userModel);
    }

    private TimeSeriesGraphDataResponse getDiastoleGraphData(UserModel userModel) {
        return getBloodPressureGraphData(BloodPressureReading::getDiastole, userModel);
    }

    private TimeSeriesGraphDataResponse getHeartRateGraphData(UserModel userModel) {
        return getBloodPressureGraphData(BloodPressureReading::getHeartRate, userModel);
    }

    private TimeSeriesGraphDataResponse getBloodPressureGraphData(ToIntFunction<BloodPressureReading> bpDataAccessorFunction, UserModel userModel) {
        List<BloodPressureReading> bloodPressureReadings = getBloodPressureReadings(userModel);
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
