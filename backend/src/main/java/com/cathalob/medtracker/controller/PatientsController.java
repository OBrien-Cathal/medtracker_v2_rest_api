package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.exception.validation.PatientRegistrationException;
import com.cathalob.medtracker.mapper.PatientRegistrationMapper;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import com.cathalob.medtracker.payload.request.patient.ApprovePatientRegistrationRequest;
import com.cathalob.medtracker.payload.request.patient.PatientRegistrationRequest;
import com.cathalob.medtracker.payload.response.patient.ApprovePatientRegistrationResponse;
import com.cathalob.medtracker.payload.response.patient.PatientRegistrationResponse;
import com.cathalob.medtracker.service.impl.PatientsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientsController {

    private final PatientsService patientsService;
    private final PatientRegistrationMapper patientRegistrationMapper;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<List<UserModel>> getPatientUserModels(Authentication authentication) {
        return ResponseEntity.ok(patientsService.getPatientUserModelsForPractitioner(authentication.getName()));
    }

    @PreAuthorize("hasRole('ROLE_PRACTITIONER')||hasRole('ROLE_PATIENT')||hasRole('ROLE_USER')")
    @GetMapping("/registrations")
    public ResponseEntity<List<PatientRegistrationData>> getPatientRegistrations(Authentication authentication) {
        return ResponseEntity.ok(
                patientRegistrationMapper.patientRegistrationData(
                        patientsService.getPatientRegistrations(authentication.getName())));
    }

    @PostMapping("/registrations/submit")
    @PreAuthorize("hasRole('ROLE_USER')||hasRole('ROLE_PATIENT')")
    public ResponseEntity<PatientRegistrationResponse> registerPatient(
            Authentication authentication,
            @RequestBody PatientRegistrationRequest request) {

        try {
            return ResponseEntity.ok(PatientRegistrationResponse.Success(
                    patientsService.registerPatient(authentication.getName(),
                            request.getPractitionerId())));
        } catch (PatientRegistrationException e) {
            return ResponseEntity.ok(PatientRegistrationResponse.Failed(e.getErrors()));
        }
    }

    @PostMapping("/registrations/approve")
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<ApprovePatientRegistrationResponse> approvePatientRegistration(Authentication authentication, @RequestBody ApprovePatientRegistrationRequest request) {

        try {
            return ResponseEntity.ok(ApprovePatientRegistrationResponse.Success(patientsService.approvePatientRegistration(authentication.getName(),
                    request.getPatientRegistrationId())));
        } catch (PatientRegistrationException e) {
            return ResponseEntity.ok(ApprovePatientRegistrationResponse.Failed(request.getPatientRegistrationId(), e.getErrors()));
        }
    }
}
