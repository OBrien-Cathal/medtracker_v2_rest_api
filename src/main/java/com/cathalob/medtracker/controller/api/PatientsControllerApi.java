package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import com.cathalob.medtracker.payload.request.ApprovePatientRegistrationRequest;
import com.cathalob.medtracker.payload.request.PatientRegistrationRequest;
import com.cathalob.medtracker.payload.response.ApprovePatientRegistrationResponse;
import com.cathalob.medtracker.payload.response.PatientRegistrationResponse;
import com.cathalob.medtracker.service.api.impl.PatientsServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientsControllerApi {

    private final PatientsServiceApi patientsService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<List<UserModel>> getPatientUserModels(Authentication authentication) {
        return ResponseEntity.ok(patientsService.getPatientUserModelsForPractitioner(authentication.getName()));
    }

    @GetMapping("/registrations")
    public ResponseEntity<List<PatientRegistrationData>> getPatientRegistrations(Authentication authentication) {
        return ResponseEntity.ok(patientsService.getPatientRegistrations(authentication.getName()));
    }

    @PostMapping("/registrations/submit")
    public ResponseEntity<PatientRegistrationResponse> registerPatient(
            Authentication authentication,
            @RequestBody PatientRegistrationRequest request) {
        return ResponseEntity.ok(patientsService.registerPatient(authentication.getName(), request.getPractitionerId()));
    }

    @PostMapping("/registrations/approve")
    public ResponseEntity<ApprovePatientRegistrationResponse> approvePatientRegistration(Authentication authentication, @RequestBody ApprovePatientRegistrationRequest request) {
        return ResponseEntity.ok(patientsService.approvePatientRegistration(authentication.getName(), request.getPatientRegistrationId()));
    }


}
