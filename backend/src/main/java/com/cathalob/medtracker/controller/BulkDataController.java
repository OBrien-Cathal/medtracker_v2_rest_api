package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.payload.response.generic.GenericResponse;
import com.cathalob.medtracker.service.impl.BulkDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/bulk-data")
@RequiredArgsConstructor
@RestController
public class BulkDataController {
    private final BulkDataService bulkDataService;


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
}
