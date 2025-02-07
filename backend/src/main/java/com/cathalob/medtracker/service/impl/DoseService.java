package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.validation.dose.DailyDoseDataException;
import com.cathalob.medtracker.mapper.DailyEvaluationMapper;
import com.cathalob.medtracker.mapper.DoseMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;

import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.payload.data.GraphData;
import com.cathalob.medtracker.payload.request.graph.GraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.patient.AddDailyDoseDataRequest;
import com.cathalob.medtracker.payload.request.patient.GetDailyDoseDataRequest;
import com.cathalob.medtracker.payload.response.AddDailyDoseDataRequestResponse;
import com.cathalob.medtracker.payload.response.GetDailyDoseDataRequestResponse;
import com.cathalob.medtracker.payload.response.TimeSeriesGraphDataResponse;
import com.cathalob.medtracker.repository.DailyEvaluationRepository;
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
    private final DailyEvaluationRepository dailyEvaluationRepository;
    private final PrescriptionScheduleEntryRepository prescriptionScheduleEntryRepository;
    private final DoseMapper doseMapper;
    private final DailyEvaluationMapper dailyEvaluationMapper;


    public List<Dose> getDoses(UserModel userModel) {
        return doseRepository.findDosesForUserId(userModel.getId());
    }


    public void saveDoses(List<Dose> newDoses) {
        doseRepository.saveAll(newDoses);
    }

    public Map<Long, Dose> getDosesById() {
        return doseRepository.findAll().stream().collect(Collectors.toMap(Dose::getId, Function.identity()));
    }

    public TimeSeriesGraphDataResponse getDoseGraphData(String patientUsername, GraphDataForDateRangeRequest request) {
        return getDoseGraphDataResponse(userService.findByLogin(patientUsername), request.getStart(), request.getEnd(), false);
    }

    public TimeSeriesGraphDataResponse getPatientDoseGraphData(Long patientUserModelId, String practitionerUsername, GraphDataForDateRangeRequest request) {
        UserModel practitioner = userService.findByLogin(practitionerUsername);
        Optional<UserModel> maybePatient = userService.findUserModelById(patientUserModelId);
        if (maybePatient.isEmpty()) return TimeSeriesGraphDataResponse.Failure();
        UserModel patient = maybePatient.get();
//        validate that the practitioner is a doc of the patient, and allowed to see the patient data
        if (patientRegistrationRepository.findByUserModelAndPractitionerUserModel(patient, practitioner).isEmpty()) {
            return TimeSeriesGraphDataResponse.Failure(List.of("Only registered practitioners can view this patients data"));

        }
        return getDoseGraphDataResponse(patient, request.getStart(), request.getEnd(), false);
    }

    public GetDailyDoseDataRequestResponse getDailyDoseData(GetDailyDoseDataRequest request, String username) {
        UserModel patient = userService.findByLogin(username);

        DailyEvaluation dailyEvaluationPlaceholder = dailyEvaluationMapper.toDailyEvaluation(request.getDate(), patient);
        DailyEvaluation dailyEvaluation = dailyEvaluationRepository.findById(dailyEvaluationPlaceholder.getDailyEvaluationIdClass())
                .orElse(dailyEvaluationRepository.save(dailyEvaluationPlaceholder));

        List<Prescription> prescriptionsValidOnRequestDate = prescriptionsService.getPrescriptionsValidOnDate(patient, request.getDate());

        List<PrescriptionScheduleEntry> prescriptionScheduleEntries = prescriptionScheduleEntryRepository
                .findByPrescriptionIds(prescriptionsValidOnRequestDate.stream().map(Prescription::getId).toList());

        return GetDailyDoseDataRequestResponse.Success(LocalDate.now(),
                doseMapper.dailyMedicationDoseDataList(prescriptionScheduleEntries, doseRepository.findByEvaluation(dailyEvaluation)));
    }

    public AddDailyDoseDataRequestResponse addDailyDoseData(AddDailyDoseDataRequest request, String username) throws DailyDoseDataException {
        UserModel patient = userService.findByLogin(username);

        DailyEvaluation dailyEvaluationPlaceholder = dailyEvaluationMapper.toDailyEvaluation(request.getDate(), patient);
        DailyEvaluation dailyEvaluation = dailyEvaluationRepository.findById(dailyEvaluationPlaceholder.getDailyEvaluationIdClass())
                .orElse(dailyEvaluationRepository.save(dailyEvaluationPlaceholder));

        PrescriptionScheduleEntry prescriptionScheduleEntry = prescriptionScheduleEntryRepository.findById(request.getDailyDoseData().getPrescriptionScheduleEntryId()).orElse(null);

        List<Dose> byId = doseRepository.findByPrescriptionScheduleEntryAndEvaluation(prescriptionScheduleEntry, dailyEvaluation);

        Dose addOrUpdateDose = doseMapper.dose(request, dailyEvaluation, prescriptionScheduleEntry);

        if (byId.isEmpty()) {
            DoseValidator.AddDoseValidator(addOrUpdateDose).validate();
        } else {
            Dose foundDose = byId.get(0);
            addOrUpdateDose.setId(foundDose.getId());
            DoseValidator.UpdateDoseValidator(addOrUpdateDose, foundDose).validate();
        }
        Dose saved = doseRepository.save(addOrUpdateDose);

        return AddDailyDoseDataRequestResponse.Success(LocalDate.now(), saved.getId());
    }

    private Map<LocalDate, List<Dose>> existingDoses(UserModel patient, LocalDate start, LocalDate end) {

        List<DailyEvaluation> existingDailyEvaluations = dailyEvaluationRepository.findDailyEvaluationsByUserModel(patient)
                .stream()
                .filter(dailyEvaluation ->

                        (dailyEvaluation.getRecordDate().isEqual(start) || dailyEvaluation.getRecordDate().isAfter(start))
                                &&
                                (dailyEvaluation.getRecordDate().isEqual(end) || dailyEvaluation.getRecordDate().isBefore(end))).toList();

        return existingDailyEvaluations
                .stream()
                .collect(Collectors.toMap(DailyEvaluation::getRecordDate, doseRepository::findByEvaluation));
    }

    private Map<LocalDate, List<Dose>> dummyDosesForRange(UserModel patient, LocalDate start, LocalDate end) {

        HashMap<LocalDate, List<PrescriptionScheduleEntry>> entriesByDate =
                prescriptionsService.getPrescriptionScheduleEntriesValidBetween(patient, start, end);

        return doseMapper.dummyDosesForRange(entriesByDate);
    }


    private TimeSeriesGraphDataResponse getDoseGraphDataResponse(UserModel patient, LocalDate start, LocalDate end, boolean interpolate) {

        if (start == null || end == null) {
            return TimeSeriesGraphDataResponse.Failure(List.of("No date range provided"));
        }

        Map<LocalDate, List<Dose>> existingDosesByDate = existingDoses(patient, start, end);
        Map<LocalDate, List<Dose>> dummyDosesByDate = dummyDosesForRange(patient, start, end);

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

        return TimeSeriesGraphDataResponse.Success(
                getDoseGraphData(orderedDosesMap));
    }

    private GraphData getDoseGraphData(TreeMap<LocalDate, List<Dose>> orderedDosesMap) {
        List<Dose> doseList = orderedDosesMap.values()
                .stream()
                .flatMap(List::stream).toList();
        List<Medication> distinctMedications = doseList.stream()
                .map(dose -> dose.getPrescriptionScheduleEntry()
                        .getPrescription()
                        .getMedication())
                .distinct().sorted(Comparator.comparing(Medication::getName))
                .toList();
        List<DAYSTAGE> distinctDayStages = doseList.stream()
                .map(dose -> dose.getPrescriptionScheduleEntry()
                        .getDayStage())
                .distinct().sorted(Comparator.comparing(DAYSTAGE::ordinal))
                .toList();


        List<List<Object>> listData = new ArrayList<>();

        LinkedHashMap<String, HashMap<LocalDate, List<Integer>>> columnRegistry = new LinkedHashMap<>();

        for (LocalDate currentDate : orderedDosesMap.keySet()) {
            List<Dose> doseForCurrentDate = orderedDosesMap.get(currentDate);


            for (Medication medication : distinctMedications) {
                for (DAYSTAGE daystage : distinctDayStages) {

                    List<Dose> dosesByMedAndDayStage = doseForCurrentDate.stream()
                            .filter(dose ->
                                    dose.getPrescriptionScheduleEntry().getPrescription().getMedication().getId().equals(medication.getId())
                                            && dose.getPrescriptionScheduleEntry().getDayStage().equals(daystage)).toList();

                    String columnName = medication.nameWithDayStage(daystage);

                    columnRegistry.putIfAbsent(columnName, new HashMap<>());
                    HashMap<LocalDate, List<Integer>> localDateListHashMap = columnRegistry.get(columnName);

                    localDateListHashMap.putIfAbsent(currentDate, new ArrayList<>());
                    List<Integer> byMedDayStageAndDate = localDateListHashMap.get(currentDate);

                    if (dosesByMedAndDayStage.isEmpty()) {
                        byMedDayStageAndDate.add(null);
                    } else {

                        byMedDayStageAndDate.add(
                                dosesByMedAndDayStage.stream()
                                        .filter(Dose::isTaken)
                                        .mapToInt(value -> value.getPrescriptionScheduleEntry().getPrescription().getDoseMg())
                                        .sum());
                    }
                }
            }

        }

        LinkedHashMap<String, HashMap<LocalDate, List<Integer>>> columnsWithAtLeastOneValue = new LinkedHashMap<>();

        columnRegistry.entrySet().stream()
                .filter(column -> {

                    Collection<List<Integer>> values = column.getValue().values();
                    System.out.println("Ints colls for column: " + column.getKey() + " -- " + values);
                    boolean allEmptyForColumn = values.stream()
                            .allMatch(integersOrNulls -> {
                                System.out.println("Ints for date and column");
                                boolean allNullByDate = integersOrNulls.stream().allMatch(Objects::isNull);
                                System.out.println("All null today = " + allNullByDate);
                                return allNullByDate;
                            });
                    System.out.println("All empty for column: " + allEmptyForColumn);
                    System.out.println("Should filter column: " + !allEmptyForColumn);
                    return !allEmptyForColumn;


                }).toList()
                .forEach(stringHashMapEntry ->
                        columnsWithAtLeastOneValue.put(stringHashMapEntry.getKey(), stringHashMapEntry.getValue()));


        List<String> names = new ArrayList<>();
        names.add("Date");
        names.addAll(columnsWithAtLeastOneValue.keySet().stream().toList());

        System.out.println("All Data Columns = " + columnRegistry.keySet());
        System.out.println("Fil Data Columns = " + names);

        for (LocalDate currentDate : orderedDosesMap.keySet()) {
            List<Object> dayDoseData = new ArrayList<>();
            dayDoseData.add(currentDate);

            columnsWithAtLeastOneValue.forEach((columnName, dosesForColumn) -> {
                List<Integer> columnDosesForDate = dosesForColumn.get(currentDate);
                if (columnDosesForDate != null) dayDoseData.addAll(columnDosesForDate);
            });

            System.out.println(dayDoseData);
            listData.add(dayDoseData);
        }

        return new GraphData(
                names,
                listData);
    }

}
