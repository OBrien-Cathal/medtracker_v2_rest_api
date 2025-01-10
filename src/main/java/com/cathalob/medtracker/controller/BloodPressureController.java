package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.payload.response.TimeSeriesGraphDataResponse;
import com.cathalob.medtracker.service.impl.BloodPressureDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/blood-pressure")
@RequiredArgsConstructor
public class BloodPressureController {

    private final BloodPressureDataService bloodPressureDataService;

    @GetMapping("/systole-graph-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<TimeSeriesGraphDataResponse> getSystoleGraphData(Authentication authentication) {
        return ResponseEntity.ok(bloodPressureDataService.getSystoleGraphData(authentication.getName()));
    }

    @GetMapping("/systole-graph-data/patient")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<TimeSeriesGraphDataResponse> getPatientSystoleGraphData(
            @RequestParam(required = false, name = "id") Long patientId,
            Authentication authentication) {
        return ResponseEntity.ok(bloodPressureDataService.getPatientSystoleGraphData(patientId, authentication.getName()));
    }
}
