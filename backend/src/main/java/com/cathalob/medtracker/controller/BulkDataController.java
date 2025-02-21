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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@RequestMapping("/api/v1/bulk-data")
@RequiredArgsConstructor
@RestController
public class BulkDataController {
    private final BulkDataService bulkDataService;
    private final BulkDataMapper bulkDataMapper;

    //  Patient data ----------------------------------------------------------------
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping("/dose-upload")
    public ResponseEntity<GenericResponse> reapDoseDataFromExcelUpload(@RequestParam("dosesFile") MultipartFile reapExcelDataFile, Authentication authentication) {
        bulkDataService.importDoseFile(reapExcelDataFile, (authentication.getName()));
        return ResponseEntity.ok(GenericResponse.Success());
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping("/blood-pressure-upload")
    public ResponseEntity<GenericResponse> reapBloodPressureDataFromExcelUpload(@RequestParam("bloodPressureFile") MultipartFile reapExcelDataFile, Authentication authentication) {
        bulkDataService.importBloodPressureFile(reapExcelDataFile, (authentication.getName()));
        return ResponseEntity.ok(GenericResponse.Success());
    }


    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/blood-pressure-download")
    public ResponseEntity<ByteArrayResource> getBloodPressureFileToDownload(Authentication authentication) {
        try {
            return getResponseEntity(getBloodPressureFileContentResource(authentication), bloodPressureFileName());

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/dose-download")
    public ResponseEntity<ByteArrayResource> getDoseFileToDownload(Authentication authentication) {
        try {
            return getResponseEntity(getDoseFileContentResource(authentication), doseFileName());

        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
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

    //  Practitioner Data
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    @PostMapping("/prescriptions-upload")
    public ResponseEntity<GenericResponse> reapPrescriptionDataFromExcelUpload(@RequestParam("prescriptionsFile") MultipartFile reapExcelDataFile, Authentication authentication) {
        bulkDataService.importPrescriptionsFile(reapExcelDataFile, (authentication.getName()));
        return ResponseEntity.ok(GenericResponse.Success());
    }

    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    @PostMapping("/medications-upload")
    public ResponseEntity<GenericResponse> reapMedicationsDataFromExcelUpload(@RequestParam("medicationsFile") MultipartFile reapExcelDataFile, Authentication authentication) {
        bulkDataService.importMedicationsFile(reapExcelDataFile, (authentication.getName()));
        return ResponseEntity.ok(GenericResponse.Success());
    }


    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    @GetMapping("/medications-download")
    public ResponseEntity<ByteArrayResource> getMedicationsFileToDownload(Authentication authentication) {
        try {
            return getResponseEntity(getMedicationFileContentResource(), medicationFileName());

        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    @GetMapping("/prescriptions-download")
    public ResponseEntity<ByteArrayResource> getPrescriptionFileToDownload(Authentication authentication) {
        try {
            return getResponseEntity(getPrescriptionFileContentResource(authentication), prescriptionFileName());

        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private ByteArrayResource getMedicationFileContentResource() throws IOException {
        return bulkDataMapper.medicationFileContentResource(
                bulkDataService.getAllMedications());
    }

    private ByteArrayResource getPrescriptionFileContentResource(Authentication authentication) throws IOException {
        return bulkDataMapper.prescriptionFileContentResource(
                bulkDataService.getPrescriptionsEnteredBy(authentication.getName()));
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

    private static String dataFileExt() {
        return ".xlsx";
    }

    private static String timestampedDataFile(String filenameBase) {
        return filenameBase + "_" + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm").format(LocalDateTime.now()) + dataFileExt();
    }

    private static String bloodPressureFileName() {
        return timestampedDataFile("BloodPressure_Data");
    }

    private static String doseFileName() {
        return timestampedDataFile("Dose_Data");
    }

    private static String medicationFileName() {
        return timestampedDataFile("Medication_Data");
    }

    private static String prescriptionFileName() {
        return timestampedDataFile("Prescription_Data");
    }
}
