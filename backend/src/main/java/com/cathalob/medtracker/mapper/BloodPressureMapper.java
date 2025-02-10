package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.payload.data.BloodPressureData;
import com.cathalob.medtracker.payload.data.GraphData;
import com.cathalob.medtracker.payload.request.patient.AddDatedBloodPressureReadingRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class BloodPressureMapper {

    public static BloodPressureReading ToBloodPressureReading(AddDatedBloodPressureReadingRequest request) {
        BloodPressureData data = request.getData();
        return BloodPressureReading.builder()
                .readingTime(LocalDateTime.of(request.getDate(), LocalTime.now()))

                .systole(data.getSystole())
                .diastole(data.getDiastole())
                .heartRate(data.getHeartRate())
                .dayStage(data.getDayStage())
                .build();
    }

    public BloodPressureReading toBloodPressureReading(AddDatedBloodPressureReadingRequest request) {
        return ToBloodPressureReading(request);
    }

    public List<BloodPressureData> bloodPressureDataList(List<BloodPressureReading> readings) {
        return BloodPressureDataList(readings);
    }

    public static List<BloodPressureData> BloodPressureDataList(List<BloodPressureReading> readings) {

        return readings.stream().map((r) -> BloodPressureData.builder()
                .readingTime(r.getReadingTime())
                .systole(r.getSystole())
                .diastole(r.getDiastole())
                .heartRate(r.getHeartRate())
                .dayStage(r.getDayStage())
                .build()).toList();
    }


    public GraphData getSystoleGraphData(TreeMap<LocalDate, List<BloodPressureReading>> columnsToValues) {
        return GetGraphData(columnsToValues, BloodPressureReading::getSystole);
    }

    public static GraphData GetGraphData(TreeMap<LocalDate, List<BloodPressureReading>> datesToReadings,
                                         ToIntFunction<BloodPressureReading> getBpValueFunction) {
        datesToReadings.values().forEach(bloodPressureReadings -> System.out.println(bloodPressureReadings));

        List<List<Object>> listData = new ArrayList<>();

        List<DAYSTAGE> sortedDayStages = datesToReadings.values().stream()
                .flatMap(List::stream)
                .map(BloodPressureReading::getDayStage)
                .distinct()
                .sorted(Comparator.comparing(DAYSTAGE::ordinal)).toList();


        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("Date");
        List<String> sortedDayStageNames = sortedDayStages.stream().map(daystage -> daystage.toString().charAt(0) + daystage.toString().substring(1).toLowerCase()).toList();
        columnNames.addAll(sortedDayStageNames);
        System.out.println(columnNames);
        for (LocalDate currentDate : datesToReadings.keySet()) {
            ArrayList<Object> dayList = new ArrayList<>();
            List<BloodPressureReading> bpForCurrent = datesToReadings.get(currentDate);
            Map<DAYSTAGE, List<BloodPressureReading>> bprMap =
                    bpForCurrent.stream()
                            .collect(Collectors.groupingBy(BloodPressureReading::getDayStage));
            dayList.add(currentDate);
            for (DAYSTAGE dayStage : sortedDayStages) {
                if (bprMap.containsKey(dayStage)) {
                    OptionalDouble average = bprMap.get(dayStage).stream().mapToInt(getBpValueFunction).average();
                    dayList.add(average.isEmpty() ? null : ((int) average.getAsDouble()));
                } else {
                    dayList.add(null);
                }
            }
            System.out.println(currentDate +""+ dayList);
            listData.add(dayList);
        }
        return new GraphData(columnNames, listData);
    }
}