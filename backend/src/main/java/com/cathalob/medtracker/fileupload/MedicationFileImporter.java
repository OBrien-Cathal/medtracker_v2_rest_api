package com.cathalob.medtracker.fileupload;

import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.service.impl.MedicationsService;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;

public class MedicationFileImporter extends FileImporter {

    private final MedicationsService medicationsService;

    public MedicationFileImporter(String username, MedicationsService medicationsService) {
        super(username);
        this.medicationsService = medicationsService;
    }

    @Override
    public void processWorkbook(XSSFWorkbook workbook) {
        List<Medication> newMedications = new ArrayList<>();

        workbook.forEach(sheet -> {

            DataFormatter dataFormatter = new DataFormatter();
            int index = 0;
            for (Row row : sheet) {
                if (index++ == 0) continue;
                Medication medication = new Medication();
                if (row.getCell(0) != null) {
                    medication.setName(dataFormatter.formatCellValue(row.getCell(0)));
                }

                newMedications.add(medication);
            }
        });

        medicationsService.addMedications(username, newMedications);
    }
}
