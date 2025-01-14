package com.cathalob.medtracker.fileupload;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

@Setter
@Slf4j
public abstract class FileImporter {
    public ImportCache importCache;

    public FileImporter() {
    }

    public FileImporter(ImportCache importCache) {
        this.importCache = importCache;
    }

    public void processMultipartFile(MultipartFile fileToImport) {
        this.logProcessing(importCache.getUserModel().getUsername(), fileToImport.getOriginalFilename());
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fileToImport.getInputStream());
            this.processWorkbook(workbook);
        } catch (EncryptedDocumentException | IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (workbook != null) workbook.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    public void processFileNamed(String filename) {
        this.logProcessing("System", filename);
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(new FileInputStream(filename));
            this.processWorkbook(workbook);
        } catch (EncryptedDocumentException | IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (workbook != null) workbook.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void logProcessing(String username, String filename){
        log.info(this.getClass() + " User: " + username + " FN: " + filename);
    }
    abstract public void processWorkbook(XSSFWorkbook workbook);

}
