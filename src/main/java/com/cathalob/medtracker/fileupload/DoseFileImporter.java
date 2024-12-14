package com.cathalob.medtracker.fileupload;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.service.impl.DoseService;
import com.cathalob.medtracker.service.impl.EvaluationDataService;
import com.cathalob.medtracker.service.impl.PrescriptionsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DoseFileImporter extends FileImporter {
    private final EvaluationDataService evaluationDataService;
    private final PrescriptionsService prescriptionsService;
    private final DoseService doseService;

    public DoseFileImporter(UserModel userModel, EvaluationDataService evaluationDataService, PrescriptionsService prescriptionsService, DoseService doseService) {
        super(new ImportCache());
        this.doseService = doseService;
        importCache.setUserModel(userModel);
        this.evaluationDataService = evaluationDataService;
        this.prescriptionsService = prescriptionsService;

        importCache.loadPrescriptions(this.prescriptionsService);
        importCache.loadPrescriptionScheduleEntries(this.prescriptionsService);
        importCache.loadDailyEvaluations(this.evaluationDataService);
        importCache.loadDoses(this.doseService);

    }

    public DoseFileImporter(EvaluationDataService evaluationDataService, PrescriptionsService prescriptionsService, DoseService doseService) {
        super();
        this.evaluationDataService = evaluationDataService;
        this.prescriptionsService = prescriptionsService;
        this.doseService = doseService;
    }

    @Override
    public void processWorkbook(XSSFWorkbook workbook) {
        List<Dose> newDoses = new ArrayList<>();
//            log.info("Number of sheets: " + workbook.getNumberOfSheets());

        workbook.forEach(sheet -> {
//                log.info("Title of sheet => " + sheet.getSheetName());


            int index = 0;
            for (Row row : sheet) {
                if (index++ == 0) continue;
                Dose dose = new Dose();
                LocalDate localDate = LocalDate.now();
                LocalTime localTime = LocalTime.now();

                if (row.getCell(0) != null) {
                    localDate = LocalDate.ofInstant(
                            row.getCell(0).getDateCellValue().toInstant(), ZoneId.systemDefault());
                }
                if (row.getCell(1) != null) {
                    long numericCellValue = (long) ((int) row.getCell(1).getNumericCellValue());
                    PrescriptionScheduleEntry prescriptionScheduleEntry = importCache.getPrescriptionScheduleEntry(numericCellValue);
                    dose.setPrescriptionScheduleEntry(prescriptionScheduleEntry);
                }

                if (row.getCell(2) != null) {
                    boolean booleanCellValue = row.getCell(2).getBooleanCellValue();
                    dose.setTaken(booleanCellValue);
                }
                if (row.getCell(3) != null) {
                    localTime = (row.getCell(3).getLocalDateTimeCellValue().toLocalTime());
                }
                dose.setDoseTime(LocalDateTime.of(localDate, localTime));
                newDoses.add(dose);
            }
        });

        List<LocalDate> dates = newDoses.stream().map(Dose::getDoseTime).map(LocalDateTime::toLocalDate).distinct().toList();

        importCache.ensureDailyEvaluations(dates, evaluationDataService);

        newDoses.forEach(dose ->
                dose.setEvaluation(importCache.getDailyEvaluation(dose.getDoseTime().toLocalDate()))
        );

        doseService.saveDoses(newDoses);
        newDoses.forEach(dose ->
                importCache.getDoses().put(dose.getId(), dose)
        );
    }


}
