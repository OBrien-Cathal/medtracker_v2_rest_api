package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.Dose;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class BulkDataMapper {

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
        XSSFSheet sheet = workbook.createSheet("Blood Pressure");

        addExcelFileColumnHeaderCells(sheet, List.of("Date", "PrescriptionScheduleEntry", "Taken", "Time"));

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

//         DayStage
            row.createCell(1, CellType.NUMERIC).setCellValue(dose.getPrescriptionScheduleEntry().getId());
//         Taken
            row.createCell(2, CellType.BOOLEAN).setCellValue(dose.isTaken());


//        Time
            XSSFCell timeCell = row.createCell(3);
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
