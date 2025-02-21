package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.puremodel.PrescriptionDetails;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class BulkDataMapper {


    public ByteArrayResource medicationFileContentResource(List<Medication> medications) throws IOException {
        return new ByteArrayResource(medicationFileContent(medications));
    }

    public byte[] medicationFileContent(List<Medication> medications) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Medication");

        addExcelFileColumnHeaderCells(sheet, List.of("Medication Name"));

        int rowIndex = 1;
        for (Medication medication : medications) {

            XSSFRow row = sheet.createRow(rowIndex++);
//  Medication
            row.createCell(0, CellType.STRING).setCellValue(medication.getName());

        }

        workbook.write(stream);
        workbook.close();

        return stream.toByteArray();
    }

    public ByteArrayResource prescriptionFileContentResource(List<PrescriptionDetails> prescriptionDetails) throws IOException {
        return new ByteArrayResource(prescriptionFileContent(prescriptionDetails));
    }

    public byte[] prescriptionFileContent(List<PrescriptionDetails> prescriptionDetails) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Prescriptions");

        addExcelFileColumnHeaderCells(sheet, List.of("Medication ID", "Patient ID", "Practitioner Name", "Begin Time", "End Time", "Dose Mg", "Day Stages"));

        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle styleTime = workbook.createCellStyle();
        styleTime.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd_HH:mm:ss"));


        int rowIndex = 1;
        for (PrescriptionDetails prescriptionDetail : prescriptionDetails) {

            XSSFRow row = sheet.createRow(rowIndex++);
//  Medication

            Prescription prescription = prescriptionDetail.getPrescription();
            row.createCell(0, CellType.NUMERIC).setCellValue(prescription.getMedication().getId());

//      Patient ID -Long
            row.createCell(1, CellType.NUMERIC).setCellValue(prescription.getPatient().getId());

//            Practitioner UserName
            row.createCell(2, CellType.STRING).setCellValue(prescription.getPractitioner().getUsername());

//      Begin Time
            XSSFCell beginTimeCell = row.createCell(3);
            beginTimeCell.setCellValue(prescription.getBeginTime());
            beginTimeCell.setCellStyle(styleTime);

//      End Time
            XSSFCell endTimeCell = row.createCell(4);
            endTimeCell.setCellValue(prescription.getEndTime());
            endTimeCell.setCellStyle(styleTime);

//        Dose MG
            row.createCell(5, CellType.NUMERIC).setCellValue(prescription.getDoseMg());

//        DayStages

            List<String> dsNames = prescriptionDetail.getPrescriptionScheduleEntries().stream()
                    .map(prescriptionScheduleEntry -> prescriptionScheduleEntry.getDayStage().name())
                    .toList();
            row.createCell(6, CellType.STRING).setCellValue(String.join(",", dsNames));
        }


        workbook.write(stream);
        workbook.close();

        return stream.toByteArray();

    }


    public ByteArrayResource bloodPressureFileContentResource(List<BloodPressureReading> readings) throws IOException {
        return new ByteArrayResource(bloodPressureFileContent(readings));
    }

    public ByteArrayResource doseFileContentResource(List<Dose> readings) throws IOException {
        return new ByteArrayResource(doseFileContent(readings));
    }


    public byte[] bloodPressureFileContent(List<BloodPressureReading> readings) throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Blood Pressure");

        addExcelFileColumnHeaderCells(sheet, List.of("Date", "Day Stage", "Time", "Systole", "Diastole", "Heart Rate"));

        CreationHelper createHelper = workbook.getCreationHelper();

        CellStyle styleDate = workbook.createCellStyle();
        styleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

        CellStyle styleTime = workbook.createCellStyle();
        styleTime.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm"));


        CellStyle styleWarn = workbook.createCellStyle();
        styleWarn.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        styleWarn.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        CellStyle styleDanger = workbook.createCellStyle();
        styleDanger.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        styleDanger.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        int rowIndex = 1;
        for (BloodPressureReading reading : readings) {

            XSSFRow row = sheet.createRow(rowIndex++);

//        Date
            XSSFCell dateCell = row.createCell(0);
            dateCell.setCellValue(reading.getDailyEvaluation().getRecordDate());
            dateCell.setCellStyle(styleDate);

//         DayStage
            row.createCell(1, CellType.STRING).setCellValue(reading.getDayStage().name());

//        Time
            XSSFCell timeCell = row.createCell(2);
            timeCell.setCellValue(reading.getReadingTime());
            timeCell.setCellStyle(styleTime);

//        Systole
            XSSFCell systoleCell = row.createCell(3, CellType.NUMERIC);
            systoleCell.setCellValue(reading.getSystole());
            addWarnOrDangerWhen(styleWarn, styleDanger, 125, 130, reading.getSystole(), systoleCell);

//        Diastole
            XSSFCell diastoleCell = row.createCell(4, CellType.NUMERIC);
            diastoleCell.setCellValue(reading.getDiastole());
            addWarnOrDangerWhen(styleWarn, styleDanger, 90, 100, reading.getDiastole(), diastoleCell);

//        HeartRate
            XSSFCell heartRateCell = row.createCell(5, CellType.NUMERIC);
            heartRateCell.setCellValue(reading.getHeartRate());
            addWarnOrDangerWhen(styleWarn, styleDanger, 80, 90, reading.getHeartRate(), heartRateCell);
        }


        workbook.write(stream);
        workbook.close();

        return stream.toByteArray();

    }

    public byte[] doseFileContent(List<Dose> readings) throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Dose");

        addExcelFileColumnHeaderCells(sheet, List.of("Date", "Prescription ID", "Day Stage", "Taken", "Time"));

        CreationHelper createHelper = workbook.getCreationHelper();

        CellStyle styleDate = workbook.createCellStyle();
        styleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

        CellStyle styleTime = workbook.createCellStyle();
        styleTime.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm"));

        int rowIndex = 1;
        for (Dose dose : readings) {

            XSSFRow row = sheet.createRow(rowIndex++);

//        Date
            XSSFCell dateCell = row.createCell(0);
            dateCell.setCellValue(dose.getEvaluation().getRecordDate());
            dateCell.setCellStyle(styleDate);
//          Prescription ID
            row.createCell(1, CellType.NUMERIC).setCellValue(dose.getPrescriptionScheduleEntry().getPrescription().getId());
//         DayStage
            row.createCell(2, CellType.STRING).setCellValue(dose.getPrescriptionScheduleEntry().getDayStage().name());
//         Taken
            row.createCell(3, CellType.BOOLEAN).setCellValue(dose.isTaken());


//        Time
            XSSFCell timeCell = row.createCell(4);
            timeCell.setCellValue(dose.getDoseTime());
            timeCell.setCellStyle(styleTime);
        }


        workbook.write(stream);
        workbook.close();

        return stream.toByteArray();

    }


    private void addWarnOrDangerWhen(CellStyle styleWarn, CellStyle styleDanger, Integer warnThreshold, Integer dangerThreshold, Integer value, XSSFCell cell) {
        if (value > dangerThreshold) {
            cell.setCellStyle(styleDanger);
        } else if (value > warnThreshold) {
            cell.setCellStyle(styleWarn);
        }
    }

    private void addExcelFileColumnHeaderCells(XSSFSheet sheet, List<String> columnNames) {
        XSSFRow row = sheet.createRow(0);
//        System.out.println("COLSIze: " + columnNames.size());
        for (int i = 0; i < columnNames.size(); i++) {
//            System.out.println("col: "+ (i) + " Name: " + columnNames.get(i));

            row.createCell(i, CellType.STRING).setCellValue(columnNames.get(i));
        }

    }


}
