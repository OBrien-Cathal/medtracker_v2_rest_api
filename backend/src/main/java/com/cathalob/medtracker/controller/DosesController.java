package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.payload.response.TimeSeriesGraphDataResponse;
import com.cathalob.medtracker.service.impl.DoseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doses")
@RequiredArgsConstructor
public class DosesController {
    private final DoseService doseService;

    @GetMapping("/graph-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<TimeSeriesGraphDataResponse> getDoseGraphData(Authentication authentication) {
        return ResponseEntity.ok(doseService.getDoseGraphData(authentication.getName()));
    }


    @GetMapping("/graph-data/patient")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<TimeSeriesGraphDataResponse> getPatientDoseGraphData(
            @RequestParam(required = false, name = "id") Long patientId,
            Authentication authentication) {
        return ResponseEntity.ok(doseService.getPatientDoseGraphData(patientId, authentication.getName()));
    }
}
