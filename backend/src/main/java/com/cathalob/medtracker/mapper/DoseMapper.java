package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.payload.data.DailyDoseData;
import com.cathalob.medtracker.payload.data.DailyMedicationDoseData;
import com.cathalob.medtracker.payload.data.GraphData;
import com.cathalob.medtracker.payload.request.patient.AddDailyDoseDataRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class DoseMapper {


    public static Dose Dose(AddDailyDoseDataRequest addDailyDoseDataRequest) {

        Dose dose = new Dose();
        dose.setDoseTime(LocalDateTime.of(addDailyDoseDataRequest.getDate(), LocalTime.now()));
        dose.setTaken(addDailyDoseDataRequest.getDailyDoseData().isTaken());
        return dose;
    }


    public static DailyMedicationDoseData DailyMedicationDoseData(Prescription prescription,
                                                                  List<DailyDoseData> doses) {
        return DailyMedicationDoseData.builder()
                .doseMg(prescription.getDoseMg())
                .medicationName(prescription.getMedication().getName())
                .prescriptionId(prescription.getId())
                .doses(doses)
                .build();
    }

    public static DailyDoseData DoseData(Dose dose) {

        return DailyDoseData.builder()
                .doseId(dose.getId())
                .prescriptionScheduleEntryId(dose.getPrescriptionScheduleEntry().getId())
                .taken(dose.isTaken())
                .doseTime(dose.getDoseTime())
                .dayStage(dose.getPrescriptionScheduleEntry().getDayStage().name())
                .build();
    }

    public Dose dose(AddDailyDoseDataRequest addDailyDoseDataRequest) {
        return DoseMapper.Dose(addDailyDoseDataRequest);
    }


    public List<DailyMedicationDoseData> dailyMedicationDoseDataList(List<Dose> doses) {
        return DoseMapper.DailyMedicationDoseDataList(doses);
    }


    public static List<DailyMedicationDoseData> DailyMedicationDoseDataList(
            List<Dose> doses) {

        Map<Prescription, List<Dose>> prescriptionListMap = doses.stream()
                .collect(Collectors.groupingBy(dose -> dose.getPrescriptionScheduleEntry().getPrescription()));

        return prescriptionListMap.entrySet().stream().map((prescriptionListEntry) -> {
            Prescription prescription = prescriptionListEntry.getKey();

            return DailyMedicationDoseData.builder()
                    .doseMg(prescription.getDoseMg())
                    .medicationName(prescription.getMedication().getName())
                    .prescriptionId(prescription.getId())
                    .doses(prescriptionListEntry.getValue().stream()
                            .map(DoseMapper::DoseData
                            ).toList())
                    .build();
        }).sorted(Comparator.comparing(DailyMedicationDoseData::getMedicationName)).toList();


    }

    public GraphData getDoseGraphData(TreeMap<LocalDate, List<Dose>> orderedDosesMap) {
        return DoseMapper.GetDoseGraphData(orderedDosesMap);
    }

    public static GraphData GetDoseGraphData(TreeMap<LocalDate, List<Dose>> orderedDosesMap) {
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
