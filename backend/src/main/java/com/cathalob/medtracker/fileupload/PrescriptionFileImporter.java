package com.cathalob.medtracker.fileupload;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.puremodel.PrescriptionImport;
import com.cathalob.medtracker.service.impl.PrescriptionsService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrescriptionFileImporter extends FileImporter {

    private final PrescriptionsService prescriptionsService;

    public PrescriptionFileImporter(String username, PrescriptionsService prescriptionsService) {
        super(username);
        this.prescriptionsService = prescriptionsService;
    }


    @Override
    public void processWorkbook(XSSFWorkbook workbook) {
        List<PrescriptionImport> prescriptionImports = new ArrayList<>();

        workbook.forEach(sheet -> {
//                log.info("Title of sheet => " + sheet.getSheetName());
            int index = 0;
            for (Row row : sheet) {
                if (index++ == 0) continue;

                Prescription prescription = new Prescription();


                Long medicationId = null;
                if (row.getCell(0) != null) {
                    medicationId = (long) ((int) row.getCell(0).getNumericCellValue());
                }

                Long patientId = null;
                if (row.getCell(1) != null) {
                    patientId = (long) ((int) row.getCell(1).getNumericCellValue());
                }

                String practitionerUserName = null;
                if (row.getCell(2) != null) {
                    practitionerUserName = row.getCell(2).getStringCellValue();
                }

                if (row.getCell(3) != null) {
                    LocalDateTime localDateTimeCellValue = row.getCell(3).getLocalDateTimeCellValue();
                    prescription.setBeginTime(localDateTimeCellValue);
                }
                if (row.getCell(4) != null) {
                    LocalDateTime localDateTimeCellValue = row.getCell(4).getLocalDateTimeCellValue();
                    prescription.setEndTime(localDateTimeCellValue);
                }

                if (row.getCell(5) != null) {
                    int localDateTimeCellValue = (int) row.getCell(5).getNumericCellValue();
                    prescription.setDoseMg(localDateTimeCellValue);
                }
                List<DAYSTAGE> daystageList = null;

                if (row.getCell(6) != null) {
                    String dayStages = row.getCell(6).getStringCellValue();
                    daystageList = Arrays.stream(dayStages.split(",")).map(DAYSTAGE::valueOf).toList();
                }


                prescriptionImports.add(PrescriptionImport.builder()
                        .medicationId(medicationId)
                        .practitionerUserName(practitionerUserName)
                        .patientId(patientId)
                        .prescription(prescription)
                        .dayStageList(daystageList)
                        .build());
            }
        });

        prescriptionsService.savePrescriptions(username, prescriptionImports);
    }
}
