package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.payload.response.Response;

import com.cathalob.medtracker.service.api.impl.PrescriptionsService;
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
    @PreAuthorize("hasRole('ROLE_PRACTITIONER') || hasRole('ROLE_PATIENT')")
    public ResponseEntity<List<Prescription>> getPrescriptions(
            Authentication authentication) {
        List<Prescription> prescriptions =
                prescriptionsService.getPrescriptions(authentication.getName());
        return ResponseEntity.ok(prescriptions);
    }

    @PostMapping("/add")
    public ResponseEntity<Response> addPrescription(
            Authentication authentication) {
        Response requestResponse = new Response(
                true,
                "Stub");
        return ResponseEntity.ok(requestResponse);
    }
}
