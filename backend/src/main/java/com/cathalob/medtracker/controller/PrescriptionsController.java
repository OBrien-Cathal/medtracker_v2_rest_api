package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.mapper.PrescriptionMapper;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.payload.data.PrescriptionDetailsData;
import com.cathalob.medtracker.payload.data.PrescriptionOverviewData;
import com.cathalob.medtracker.payload.response.SubmitPrescriptionDetailsResponse;
import com.cathalob.medtracker.payload.response.GetPrescriptionDetailsResponse;

import com.cathalob.medtracker.service.impl.PrescriptionsService;
import jakarta.validation.Valid;
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

    private final PrescriptionMapper prescriptionMapper;

    private final PrescriptionsService prescriptionsService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<List<PrescriptionOverviewData>> getPrescriptions(
            Authentication authentication) {

        return ResponseEntity.ok(
                prescriptionMapper.overviews(
                        prescriptionsService.getPrescriptions(authentication.getName())));
    }

    @GetMapping("/patient")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<List<PrescriptionOverviewData>> getPatientPrescriptions(@RequestParam(required = false, name = "id") Long patientId, Authentication authentication) {

        return ResponseEntity.ok(
                prescriptionMapper.overviews(
                        prescriptionsService.getPatientPrescriptions(authentication.getName(), patientId)));
    }


    @PostMapping("/submit-prescription")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<SubmitPrescriptionDetailsResponse> submitPrescription(Authentication authentication,
                                                                                @RequestBody @Valid PrescriptionDetailsData prescriptionDetailsData) {

        return ResponseEntity.ok(prescriptionMapper.submitPrescriptionResponse(
                prescriptionsService.submitPrescription(authentication.getName(),
                        prescriptionMapper.prescription(prescriptionDetailsData),
                        prescriptionDetailsData.getPrescriptionScheduleEntries(),
                        prescriptionDetailsData.getPatientId(),
                        prescriptionDetailsData.getMedication().getId())));
    }


    @GetMapping("/prescription-details")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')||hasRole('ROLE_PATIENT')")
    public ResponseEntity<GetPrescriptionDetailsResponse> getPrescriptionDetails(
            @RequestParam(required = false, name = "id") Long prescriptionId,
            Authentication authentication) {

        return ResponseEntity.ok(
                prescriptionMapper.getPrescriptionDetailsResponse(
                        prescriptionsService.getPrescriptionDetails(authentication.getName(),
                                prescriptionId)));
    }

    @GetMapping("/day-stages")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')||hasRole('ROLE_PATIENT')")
    public ResponseEntity<List<String>> getDayStages() {
        return ResponseEntity.ok(Arrays.stream(DAYSTAGE.values()).map(DAYSTAGE::name).toList());
    }
}
