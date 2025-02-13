package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.service.impl.AccountRegistrationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/account-registration")
@RequiredArgsConstructor

public class AccountRegistrationController {
    private final AccountRegistrationService accountRegistrationService;

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmAccountRegistration(@RequestParam(name = "reg") @NotNull UUID regId,
                                                             @RequestParam(name = "user-id") @NotNull Long userId) {

        boolean isConfirmed = accountRegistrationService.confirmRegistration(regId, userId);

        return ResponseEntity.ok(isConfirmed ? "Confirmed" : "Confirmation Failed");
    }

}
