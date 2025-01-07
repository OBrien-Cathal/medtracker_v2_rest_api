package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.payload.response.GenericRequestResponse;
import com.cathalob.medtracker.service.api.impl.MedicationsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/medications")
@RequiredArgsConstructor
public class MedicationsController {
    private final MedicationsService medicationsService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public ResponseEntity<List<Medication>> getMedications() {
        return ResponseEntity.ok(medicationsService.getMedications());
    }

    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    @PostMapping("/add")
    public ResponseEntity<GenericRequestResponse> addMedication(@RequestBody @Valid Medication medication) {
        GenericRequestResponse requestResponse = medicationsService.addMedication(medication);
        return ResponseEntity.ok(requestResponse);
    }
}