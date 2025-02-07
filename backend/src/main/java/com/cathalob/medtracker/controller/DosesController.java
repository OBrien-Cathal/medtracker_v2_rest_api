package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.exception.validation.dose.DailyDoseDataException;
import com.cathalob.medtracker.payload.request.graph.GraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.graph.PatientGraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.patient.*;
import com.cathalob.medtracker.payload.response.AddDailyDoseDataRequestResponse;
import com.cathalob.medtracker.payload.response.GetDailyDoseDataRequestResponse;
import com.cathalob.medtracker.payload.response.TimeSeriesGraphDataResponse;
import com.cathalob.medtracker.service.impl.DoseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doses")
@RequiredArgsConstructor
public class DosesController {
    private final DoseService doseService;

    @PostMapping("/graph-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<TimeSeriesGraphDataResponse> getDoseGraphData(
            Authentication authentication,
            @RequestBody GraphDataForDateRangeRequest request) {
        return ResponseEntity.ok(doseService.getDoseGraphData(authentication.getName(), request));
    }

//    @GetMapping("/graph-data")
//    @PreAuthorize("hasRole('ROLE_PATIENT')")
//    public ResponseEntity<TimeSeriesGraphDataResponse> getDoseGraphDataDateRange(Authentication authentication) {
//        return ResponseEntity.ok(doseService.getDoseGraphData(authentication.getName()));
//    }


    @PostMapping("/graph-data/patient")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<TimeSeriesGraphDataResponse> getPatientDoseGraphData(

            Authentication authentication,@RequestBody PatientGraphDataForDateRangeRequest request) {
        return ResponseEntity.ok(doseService.getPatientDoseGraphData(request.getPatientId(), authentication.getName(), request ));
    }


    @PostMapping("/daily-dose-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<GetDailyDoseDataRequestResponse> getDailyDoseData(
            @RequestBody GetDailyDoseDataRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(doseService.getDailyDoseData(request, authentication.getName()));
    }

    @PostMapping("/add-daily-dose-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<AddDailyDoseDataRequestResponse> addDailyDoseData(
            @RequestBody AddDailyDoseDataRequest request,
            Authentication authentication) {
        AddDailyDoseDataRequestResponse response;
        try {
            response = (doseService.addDailyDoseData(request, authentication.getName()));

        } catch (DailyDoseDataException e) {
            response = (AddDailyDoseDataRequestResponse.Failed(request.getDate(), e.getErrors()));
        }
        return ResponseEntity.ok(response);
    }


}
