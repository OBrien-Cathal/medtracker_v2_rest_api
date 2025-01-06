package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.dto.PrescriptionDTO;
import com.cathalob.medtracker.dto.PrescriptionsDTO;
import com.cathalob.medtracker.fileupload.BloodPressureFileImporter;
import com.cathalob.medtracker.fileupload.DoseFileImporter;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.Dose;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PatientsService {
    private final PrescriptionsService prescriptionsService;
    private final BloodPressureDataService bloodPressureDataService;
    private final DoseService doseService;
    private final EvaluationDataService evaluationDataService;

    public PatientsService(PrescriptionsService prescriptionsService, BloodPressureDataService bloodPressureDataService, DoseService doseService, EvaluationDataService evaluationDataService) {
        this.prescriptionsService = prescriptionsService;
        this.bloodPressureDataService = bloodPressureDataService;
        this.doseService = doseService;
        this.evaluationDataService = evaluationDataService;
    }

    public PrescriptionsDTO getPrescriptionsDTO(UserModel patient) {
        PrescriptionsDTO prescriptionsDTO = new PrescriptionsDTO(new ArrayList<>());
        prescriptionsService.getPrescriptions().stream()
                .filter(prescription -> prescription.getPatient().getId().equals(patient.getId()))
                .forEach(prescription -> {
            PrescriptionDTO prescriptionDTO = new PrescriptionDTO();
            prescriptionDTO.setMedication(prescription.getMedication());
            prescriptionDTO.setDoseMg(prescription.getDoseMg());
            prescriptionsDTO.getPrescriptions().add(prescriptionDTO);
        });
        return prescriptionsDTO;
    }

    public void saveBloodPressureReadings(List<BloodPressureReading> bloodPressureReadings) {
        bloodPressureDataService.saveBloodPressureReadings(bloodPressureReadings);
    }

    public List<List<Object>> getDoseGraphData(UserModel userModel) {
        List<List<Object>> listData = new ArrayList<>();
        List<Dose> doses = doseService.getDoses(userModel);

        TreeMap<LocalDate, List<Dose>> byDate = doses.stream()
                .sorted(Comparator.comparing(dose -> dose.getDoseTime().toLocalDate()))
                .collect(Collectors.groupingBy(dose -> dose.getDoseTime().toLocalDate(), TreeMap::new, Collectors.toList()));

        List<Medication> medicationList = prescriptionsService.getPatientMedications(userModel).stream().sorted(Comparator.comparing(Medication::getName)).toList();
        List<DAYSTAGE> daystageList = prescriptionsService.getPatientPrescriptionDayStages(userModel).stream().sorted(Comparator.comparing(DAYSTAGE::ordinal)).toList();
        HashMap<Medication, HashSet<LocalDate>> patientPrescriptionDatesByMedication = prescriptionsService.getPatientPrescriptionDatesByMedication(userModel);

        Optional<LocalDate> start = byDate.keySet().stream().distinct().min(LocalDate::compareTo);

        Optional<LocalDate> endDose = byDate.keySet().stream().distinct().max(LocalDate::compareTo);
        LocalDate now = LocalDate.now().plusDays(1);
        LocalDate end = endDose.isPresent() && endDose.get().isAfter(now) ? endDose.get() : now;

        List<LocalDate> daysRange = new ArrayList<>();
        if (start.isPresent()) {
            long numDays = ChronoUnit.DAYS.between(start.get(), end);
            daysRange.addAll(Stream.iterate(start.get(), date -> date.plusDays(1)).limit(numDays).toList());
        }

        for (LocalDate date : daysRange) {
            List<Dose> dosesByDate = (byDate.get(date) == null) ? new ArrayList<>() : byDate.get(date);

            List<Object> dayDoseData = new ArrayList<>();
            Map<Medication, List<Dose>> byMedication = dosesByDate.stream()
                    .collect(Collectors.groupingBy(dose -> dose.getPrescriptionScheduleEntry().getPrescription().getMedication()));

            dayDoseData.add(date);
            for (Medication medication : medicationList) {
                for (DAYSTAGE daystage : daystageList) {
                    boolean isPrescribedOnDate = patientPrescriptionDatesByMedication.containsKey(medication) && patientPrescriptionDatesByMedication.get(medication).contains(date);

                    if (byMedication.containsKey(medication)) {
                        Map<DAYSTAGE, List<Dose>> byDayStage = byMedication.get(medication).stream()
                                .collect(Collectors.groupingBy(dose -> dose.getPrescriptionScheduleEntry().getDayStage()));
                        List<Dose> doseEntriesForDayStage = byDayStage
                                .get(daystage);
                        if (byDayStage.containsKey(daystage)) {
                            if (doseEntriesForDayStage.stream().filter(Dose::isTaken).toList().isEmpty()) {
                                dayDoseData.add(0);
                            } else {
                                dayDoseData.add(doseEntriesForDayStage.stream().filter(Dose::isTaken).mapToInt(value -> value.getPrescriptionScheduleEntry().getPrescription().getDoseMg()).sum());
                            }
                        } else {
                            dayDoseData.add(isPrescribedOnDate ? 0 : null);
                        }
                    } else {
                        dayDoseData.add(isPrescribedOnDate ? 0 : null);
                    }
                }
            }
            listData.add(dayDoseData);
        }
        return listData;
    }

    public List<List<Object>> getSystoleGraphData(List<BloodPressureReading> bloodPressureReadings) {
        return this.getBloodPressureGraphData(BloodPressureReading::getSystole, List.of(140,130,120), bloodPressureReadings);

    }

    public List<List<Object>> getDiastoleGraphData (List<BloodPressureReading> bloodPressureReadings) {
        return this.getBloodPressureGraphData(BloodPressureReading::getDiastole, List.of(90, 80), bloodPressureReadings);
    }

    public List<List<Object>> getHeartRateGraphData(List<BloodPressureReading> bloodPressureReadings) {
        return this.getBloodPressureGraphData(BloodPressureReading::getHeartRate, List.of(100,90,80), bloodPressureReadings);

    }

    public List<List<Object>> getBloodPressureGraphData(ToIntFunction<BloodPressureReading> getBpValueFunction, List<Integer> warningLevels, List<BloodPressureReading> bloodPressureReadings) {
        List<List<Object>> listData = new ArrayList<>();

        TreeMap<LocalDate, List<BloodPressureReading>> byDate = bloodPressureReadings.stream().sorted(Comparator.comparing(bloodPressureReading -> bloodPressureReading.getReadingTime().toLocalDate()))
                .collect(Collectors.groupingBy(bloodPressureReading -> bloodPressureReading.getReadingTime().toLocalDate(), TreeMap::new, Collectors.toList()));

        List<DAYSTAGE> sortedDayStages = bloodPressureReadings.stream().map(BloodPressureReading::getDayStage).distinct().sorted(Comparator.comparing(DAYSTAGE::ordinal)).toList();

        byDate.forEach((date, bloodPressureReadingsByDate) -> {
            ArrayList<Object> dayList = new ArrayList<>();
            dayList.add(date);
            dayList.addAll(warningLevels);
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

    public List<List<String>> getBloodPressureGraphColumnNames(List<DAYSTAGE> daystageList, List<String> warningLabels) {
        List<String> names = new ArrayList<>();
        names.addAll(warningLabels);
        names.addAll(this.prettifiedDayStageNames(daystageList.stream().sorted(Comparator.comparing(DAYSTAGE::ordinal)).toList()));
        return List.of(names);
    }

    public List<List<String>> getDoseGraphColumnNames(UserModel userModel) {
        List<String> names = new ArrayList<>();
        List<String> dayStageNames = this.prettifiedDayStageNames(prescriptionsService.getPatientPrescriptionDayStages(userModel));

        for (String medication : prescriptionsService.getPatientMedications(userModel).stream().map(Medication::getName).sorted().toList()) {
            for (String dayStage : dayStageNames) {
                names.add(medication + " (" + dayStage + ')');
            }
        }
        return List.of(names);
    }

    private List<String> prettifiedDayStageNames(List<DAYSTAGE> dayStages) {
        return dayStages.stream().map(ds -> (
                ds.toString().charAt(0) + ds.toString().substring(1).toLowerCase())).toList();
    }

}
