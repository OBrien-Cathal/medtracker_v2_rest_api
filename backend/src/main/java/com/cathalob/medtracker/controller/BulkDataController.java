package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.mapper.BulkDataMapper;
import com.cathalob.medtracker.payload.response.generic.GenericResponse;
import com.cathalob.medtracker.service.impl.BulkDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

@RequestMapping("/api/v1/bulk-data")
@RequiredArgsConstructor
@RestController
public class BulkDataController {
    private final BulkDataService bulkDataService;
    private final BulkDataMapper bulkDataMapper;



    @PostMapping("/dose-upload")
    public ResponseEntity<GenericResponse> reapDoseDataFromExcelUpload(@RequestParam("dosesFile") MultipartFile reapExcelDataFile, Authentication authentication) {
        bulkDataService.importDoseFile(reapExcelDataFile, (authentication.getName()));
        return ResponseEntity.ok(GenericResponse.Success());
    }

    @PostMapping("/blood-pressure-upload")
    public ResponseEntity<GenericResponse> reapBloodPressureDataFromExcelUpload(@RequestParam("bloodPressureFile") MultipartFile reapExcelDataFile, Authentication authentication) {
        bulkDataService.importBloodPressureFile(reapExcelDataFile, (authentication.getName()));
        return ResponseEntity.ok(GenericResponse.Success());
    }


    @GetMapping("/blood-pressure-download")
    public ResponseEntity<ByteArrayResource> getBloodPressureFileToDownload(Authentication authentication) {
        try {
            return getResponseEntity(getBloodPressureFileContentResource(authentication), bloodPressureFileName());

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @GetMapping("/dose-download")
    public ResponseEntity<ByteArrayResource> getDoseFileToDownload(Authentication authentication) {
        try {
            return getResponseEntity(getDoseFileContentResource(authentication), doseFileName());

        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/archive")
    public ResponseEntity<GenericResponse> getArchiveAllDataToEmail(Authentication authentication) throws IOException {
        HashMap<String, ByteArrayResource> attachments = new HashMap<>();

        try {
            attachments.put(BulkDataController.bloodPressureFileName(), getBloodPressureFileContentResource(authentication));
            attachments.put(BulkDataController.doseFileName(), getDoseFileContentResource(authentication));

            bulkDataService.sendDataArchiveMailMessage(attachments, authentication.getName());

            return ResponseEntity.ok(GenericResponse.Success());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static String bloodPressureFileName() {
        return "BloodPressure_DataBackup.xlsx";
    }

    private static String doseFileName() {
        return "Dose_DataBackup.xlsx";
    }

    private ByteArrayResource getDoseFileContentResource(Authentication authentication) throws IOException {
        return bulkDataMapper.doseFileContentResource(
                bulkDataService.getAllDoses(authentication.getName()));
    }

    private ByteArrayResource getBloodPressureFileContentResource(Authentication authentication) throws IOException {
        return bulkDataMapper.bloodPressureFileContentResource(
                bulkDataService.getAllBloodPressureReadings(authentication.getName()));
    }


    private ResponseEntity<ByteArrayResource> getResponseEntity(ByteArrayResource byteArrayResource, String filename) {

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return new ResponseEntity<>(byteArrayResource, header, HttpStatus.CREATED);
    }
}
