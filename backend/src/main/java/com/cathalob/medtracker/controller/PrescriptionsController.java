package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.payload.data.PrescriptionDetailsData;
import com.cathalob.medtracker.payload.data.PrescriptionOverviewData;
import com.cathalob.medtracker.payload.response.SubmitPrescriptionDetailsResponse;
import com.cathalob.medtracker.payload.response.GetPrescriptionDetailsResponse;

import com.cathalob.medtracker.service.impl.PrescriptionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
public class PrescriptionsController {

    private final PrescriptionsService prescriptionsService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<List<PrescriptionOverviewData>> getPrescriptions(
            Authentication authentication) {
        List<PrescriptionOverviewData> prescriptions =
                prescriptionsService.getPrescriptions(authentication.getName());
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/patient")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<List<PrescriptionOverviewData>> getPatientPrescriptions(@RequestParam(required = false, name = "id") Long patientId, Authentication authentication) {
        List<PrescriptionOverviewData> prescriptions =
                prescriptionsService.getPatientPrescriptions(authentication.getName(), patientId);
        return ResponseEntity.ok(prescriptions);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<SubmitPrescriptionDetailsResponse> addPrescription(@RequestBody PrescriptionDetailsData prescriptionDetailsData,
                                                                             Authentication authentication) {
        SubmitPrescriptionDetailsResponse response = prescriptionsService.addPrescriptionDetails(prescriptionDetailsData);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<SubmitPrescriptionDetailsResponse> updatePrescription(@RequestBody PrescriptionDetailsData prescriptionDetailsData,
                                                                             Authentication authentication) {
        SubmitPrescriptionDetailsResponse response = prescriptionsService.updatePrescriptionDetails(prescriptionDetailsData);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/prescription-details")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')||hasRole('ROLE_PATIENT')")
    public ResponseEntity<GetPrescriptionDetailsResponse> getPrescriptionDetails(
            @RequestParam(required = false, name = "id") Long prescriptionId,
            Authentication authentication) {

        GetPrescriptionDetailsResponse prescriptionDetails =
                prescriptionsService.getPrescriptionDetails(authentication.getName(), prescriptionId);
        return ResponseEntity.ok(prescriptionDetails);
    }

    @GetMapping("/day-stages")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')||hasRole('ROLE_PATIENT')")
    public ResponseEntity<List<String>> getDayStages( ) {
        return ResponseEntity.ok(Arrays.stream(DAYSTAGE.values()).map(DAYSTAGE::name).toList());
    }
}
