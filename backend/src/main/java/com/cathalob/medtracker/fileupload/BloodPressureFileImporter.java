package com.cathalob.medtracker.fileupload;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.service.impl.BloodPressureDataService;
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
public class BloodPressureFileImporter extends FileImporter {
    private final BloodPressureDataService bloodPressureDataService;

    public BloodPressureFileImporter(String userModelName, BloodPressureDataService bloodPressureDataService) {
        super(userModelName);
        this.bloodPressureDataService = bloodPressureDataService;
    }


    public void processWorkbook(XSSFWorkbook workbook) {
        List<BloodPressureReading> newBloodPressureReadings = new ArrayList<>();
//            log.info("Number of sheets: " + workbook.getNumberOfSheets());

        workbook.forEach(sheet -> {
//                log.info("Title of sheet => " + sheet.getSheetName());

            int index = 0;
            for (Row row : sheet) {
                if (index++ == 0) continue;
                BloodPressureReading bloodPressureReading = new BloodPressureReading();
                LocalDate localDate = LocalDate.now();
                LocalTime localTime = LocalTime.now();

                if (row.getCell(0) != null) {
                    localDate = LocalDate.ofInstant(
                            row.getCell(0).getDateCellValue().toInstant(), ZoneId.systemDefault());
                }

                if (row.getCell(1) != null) {
                    String dayStage = row.getCell(1).getStringCellValue();
                    bloodPressureReading.setDayStage(DAYSTAGE.valueOf(dayStage));
                }
                int timeCellIndex = 2;
                if (row.getCell(timeCellIndex) != null && (row.getCell(timeCellIndex).getLocalDateTimeCellValue() != null)) {
                    localTime = (row.getCell(timeCellIndex).getLocalDateTimeCellValue().toLocalTime());
                }

                int systoleIndex = 3;
                if (row.getCell(systoleIndex) != null) {
                    int numericCellValue = (int) (row.getCell(systoleIndex).getNumericCellValue());
                    bloodPressureReading.setSystole(numericCellValue);
                }
                if (row.getCell(4) != null) {
                    int numericCellValue = (int) (row.getCell(4).getNumericCellValue());
                    bloodPressureReading.setDiastole(numericCellValue);
                }
                if (row.getCell(5) != null) {
                    int numericCellValue = (int) (row.getCell(5).getNumericCellValue());
                    bloodPressureReading.setHeartRate(numericCellValue);
                }

                if (bloodPressureReading.hasData()) {
                    bloodPressureReading.setReadingTime(LocalDateTime.of(localDate, localTime));
                    newBloodPressureReadings.add(bloodPressureReading);
                }
            }
        });

        bloodPressureDataService.saveBloodPressureReadings(newBloodPressureReadings, username);
    }
}
