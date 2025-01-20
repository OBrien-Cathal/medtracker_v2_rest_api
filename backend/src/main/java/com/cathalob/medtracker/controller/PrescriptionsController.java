package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.payload.data.PrescriptionDetailsData;
import com.cathalob.medtracker.payload.data.PrescriptionOverviewData;
import com.cathalob.medtracker.payload.response.Response;

import com.cathalob.medtracker.service.impl.PrescriptionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Response> addPrescription(@RequestBody PrescriptionDetailsData prescriptionDetailsData,
                                                    Authentication authentication) {
        Response response = prescriptionsService.addPrescriptionDetails(prescriptionDetailsData);
        return ResponseEntity.ok(response);
    }
}
