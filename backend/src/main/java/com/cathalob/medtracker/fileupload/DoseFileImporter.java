package com.cathalob.medtracker.fileupload;

import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.service.impl.DoseService;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class DoseFileImporter extends FileImporter {

    private final DoseService doseService;

    public DoseFileImporter(String username, DoseService doseService) {
        super(username);
        this.doseService = doseService;
    }


    @Override
    public void processWorkbook(XSSFWorkbook workbook) {
        HashMap<Long, HashMap<String, List<Dose>>> doseListByDsByPrescriptionId = new HashMap<>();

        workbook.forEach(sheet -> {

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
                Long prescriptionId = null;
                if (row.getCell(1) != null) {
                    prescriptionId = (long) ((int) row.getCell(1).getNumericCellValue());
                }

                String dayStageName = null;
                if (row.getCell(2) != null) {
                    dayStageName = row.getCell(2).getStringCellValue();

                }

                if (row.getCell(3) != null) {
                    boolean booleanCellValue = row.getCell(3).getBooleanCellValue();
                    dose.setTaken(booleanCellValue);
                }
                if (row.getCell(4) != null) {
                    localTime = (row.getCell(4).getLocalDateTimeCellValue().toLocalTime());
                }
                dose.setDoseTime(LocalDateTime.of(localDate, localTime));

                doseListByDsByPrescriptionId.putIfAbsent(prescriptionId, new HashMap<>());
                doseListByDsByPrescriptionId.get(prescriptionId).putIfAbsent(dayStageName, new ArrayList<>(List.of(dose)));
                doseListByDsByPrescriptionId.get(prescriptionId).get(dayStageName).add(dose);
            }
        });
        doseService.saveDoses(username, doseListByDsByPrescriptionId);
    }
}
