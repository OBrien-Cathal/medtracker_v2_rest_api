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
            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "force-download"));
            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=BloodPressure_DataBackup.xlsx");
            byte[] bytes = bulkDataMapper.bloodPressureFileContent(
                    bulkDataService.getAllBloodPressureReadings(authentication.getName()));


            return new ResponseEntity<>(new ByteArrayResource(bytes), header, HttpStatus.CREATED);


        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @GetMapping("/dose-download")
    public ResponseEntity<ByteArrayResource> getDoseFileToDownload(Authentication authentication) {

        try {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "force-download"));
            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Dose_DataBackup.xlsx");
            byte[] bytes = bulkDataMapper.doseFileContent(
                    bulkDataService.getAllDoses(authentication.getName()));


            return new ResponseEntity<>(new ByteArrayResource(bytes), header, HttpStatus.CREATED);


        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
