package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.payload.response.Response;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
public class PrescriptionsController {

    @GetMapping
    public ResponseEntity<Response> getPrescriptions(
            Authentication authentication) {
        Response requestResponse = new Response(
                true,
                "Stub");
        return ResponseEntity.ok(requestResponse);
    }
    @PostMapping("/addPrescription")
    public ResponseEntity<Response> addPrescription(
            Authentication authentication) {
        Response requestResponse = new Response(
                true,
                "Stub");
        return ResponseEntity.ok(requestResponse);
    }
}
