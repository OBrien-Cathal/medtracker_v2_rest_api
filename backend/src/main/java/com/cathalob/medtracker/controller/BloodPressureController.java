package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.exception.validation.bloodpressure.AddBloodPressureDailyDataException;
import com.cathalob.medtracker.exception.validation.bloodpressure.BloodPressureDailyDataExceptionData;
import com.cathalob.medtracker.exception.validation.bloodpressure.BloodPressureGraphDataException;
import com.cathalob.medtracker.mapper.BloodPressureMapper;
import com.cathalob.medtracker.payload.request.graph.GraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.graph.PatientGraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.patient.AddDatedBloodPressureReadingRequest;
import com.cathalob.medtracker.payload.request.patient.AddDatedBloodPressureReadingRequestResponse;
import com.cathalob.medtracker.payload.request.patient.DatedBloodPressureDataRequest;
import com.cathalob.medtracker.payload.response.BloodPressureDataRequestResponse;
import com.cathalob.medtracker.payload.response.TimeSeriesGraphDataResponse;
import com.cathalob.medtracker.service.impl.BloodPressureDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/blood-pressure")
@RequiredArgsConstructor
public class BloodPressureController {
    private final BloodPressureMapper bloodPressureMapper;
    private final BloodPressureDataService bloodPressureDataService;

    @PostMapping("/systole-graph-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<TimeSeriesGraphDataResponse> getSystoleGraphDataDateRange(
            @RequestBody GraphDataForDateRangeRequest request,
            Authentication authentication) {
        try {

            return ResponseEntity.ok(TimeSeriesGraphDataResponse.Success(bloodPressureMapper.getSystoleGraphData(
                    bloodPressureDataService.getBloodPressureReadingsForDateRange(authentication.getName(), request.getStart(), request.getEnd()))));

        } catch (BloodPressureGraphDataException e) {
            return ResponseEntity.ok(TimeSeriesGraphDataResponse.Failure(e.getErrors()));
        }

    }


    @PostMapping("/systole-graph-data/patient")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<TimeSeriesGraphDataResponse> getPatientSystoleGraphDataDateRange(
            @RequestBody PatientGraphDataForDateRangeRequest request,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(TimeSeriesGraphDataResponse.Success(bloodPressureMapper.getSystoleGraphData(
                    bloodPressureDataService.getPatientBloodPressureReadingsForDateRange(request.getPatientId(),
                            authentication.getName(),
                            request.getStart(),
                            request.getEnd()))));

        } catch (BloodPressureGraphDataException e) {
            return ResponseEntity.ok(TimeSeriesGraphDataResponse.Failure(e.getErrors()));
        }

    }


    @PostMapping("/blood-pressure-daily-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<BloodPressureDataRequestResponse> getBloodPressureDailyData(
            @RequestBody DatedBloodPressureDataRequest datedBloodPressureDataRequest,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    BloodPressureDataRequestResponse.Success(
                            bloodPressureMapper.bloodPressureDataList(
                                    bloodPressureDataService.getBloodPressureData(
                                            authentication.getName(),
                                            datedBloodPressureDataRequest.getDate()
                                    ))));
        } catch (BloodPressureDailyDataExceptionData e) {
            return ResponseEntity.ok(BloodPressureDataRequestResponse.Failed(e.getErrors()));
        }


    }

    @PostMapping("/add-blood-pressure-daily-data")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<AddDatedBloodPressureReadingRequestResponse> addBloodPressureDailyData(
            @RequestBody AddDatedBloodPressureReadingRequest request,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    AddDatedBloodPressureReadingRequestResponse.Success(
                            bloodPressureDataService.addBloodPressureReading(
                                    bloodPressureMapper.toBloodPressureReading(request),
                                    request.getDate(),
                                    authentication.getName()
                            )));
        } catch (AddBloodPressureDailyDataException e) {
            return ResponseEntity.ok(AddDatedBloodPressureReadingRequestResponse.Failed(e.getErrors()));
        }

    }

}
