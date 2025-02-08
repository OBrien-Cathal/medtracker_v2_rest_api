package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.exception.validation.dose.DailyDoseDataException;
import com.cathalob.medtracker.exception.validation.dose.DoseGraphDataException;
import com.cathalob.medtracker.mapper.DoseMapper;
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

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/doses")
@RequiredArgsConstructor
public class DosesController {
    private final DoseService doseService;
    private final DoseMapper doseMapper;

    @PostMapping("/graph-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<TimeSeriesGraphDataResponse> getDoseGraphData(
            Authentication authentication,
            @RequestBody GraphDataForDateRangeRequest request) {

        try {
            return ResponseEntity.ok(
                    TimeSeriesGraphDataResponse.Success(doseMapper.getDoseGraphData(doseService.getDoseGraphData(authentication.getName(),
                            request.getStart(),
                            request.getEnd()))));
        } catch (DoseGraphDataException e) {
            return ResponseEntity.ok(TimeSeriesGraphDataResponse.Failure(e.getErrors()));
        }
    }

    @PostMapping("/graph-data/patient")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<TimeSeriesGraphDataResponse> getPatientDoseGraphData(
            Authentication authentication, @RequestBody PatientGraphDataForDateRangeRequest request) {

        try {
            return ResponseEntity.ok(
                    TimeSeriesGraphDataResponse.Success(
                            doseMapper.getDoseGraphData(doseService.getPatientDoseGraphData(request.getPatientId(),
                                    authentication.getName(),
                                    request.getStart(),
                                    request.getEnd()))));
        } catch (DoseGraphDataException e) {
            return ResponseEntity.ok(TimeSeriesGraphDataResponse.Failure(e.getErrors()));
        }
    }


    @PostMapping("/daily-dose-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<GetDailyDoseDataRequestResponse> getDailyDoseData(
            @RequestBody GetDailyDoseDataRequest request,
            Authentication authentication) {
        try {

            return ResponseEntity.ok(GetDailyDoseDataRequestResponse.Success(
                    LocalDate.now(),
                    doseMapper.dailyMedicationDoseDataList(
                            doseService.getDailyDoseData(authentication.getName(), request.getDate()))));
        } catch (DoseGraphDataException e) {
            return ResponseEntity.ok(GetDailyDoseDataRequestResponse.Failed(request.getDate(), e.getErrors()));
        }
    }

    @PostMapping("/add-daily-dose-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<AddDailyDoseDataRequestResponse> addDailyDoseData(
            @RequestBody AddDailyDoseDataRequest request,
            Authentication authentication) {

        try {
            System.out.println("try add");
            ResponseEntity<AddDailyDoseDataRequestResponse> ok = ResponseEntity.ok(AddDailyDoseDataRequestResponse.Success(request.getDate(), doseService.addDailyDoseData(authentication.getName(),
                    doseMapper.dose(request),
                    request.getDailyDoseData().getPrescriptionScheduleEntryId(),
                    request.getDate())));
            System.out.println(ok);
            return ok;


        } catch (DailyDoseDataException e) {
            System.out.println("catch add");
            return ResponseEntity.ok(AddDailyDoseDataRequestResponse.Failed(request.getDate(), e.getErrors()));
        }

    }


}
