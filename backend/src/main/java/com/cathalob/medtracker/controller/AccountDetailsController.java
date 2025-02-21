package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.mapper.AccountDetailsMapper;
import com.cathalob.medtracker.payload.data.AccountDetailsData;
import com.cathalob.medtracker.payload.response.generic.GenericResponse;
import com.cathalob.medtracker.service.impl.AccountDetailsService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/account-details")
@RequiredArgsConstructor

public class AccountDetailsController {
    private final AccountDetailsService accountDetailsService;
    private final AccountDetailsMapper accountDetailsMapper;


    @GetMapping()
    public ResponseEntity<AccountDetailsData> getAccountDetails(Authentication authentication) {
        return ResponseEntity.ok(
                accountDetailsMapper.accountDetailsData(
                        accountDetailsService.getDetails(authentication.getName())));
    }

    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    @GetMapping("/patient")
    public ResponseEntity<AccountDetailsData> getAccountDetails(@RequestParam(name = "patient-id") @NotNull Long patientId, Authentication authentication) {
        return ResponseEntity.ok(
                accountDetailsMapper.accountDetailsData(
                        accountDetailsService.getDetails(authentication.getName(), patientId)));
    }

    @PostMapping()
    public ResponseEntity<GenericResponse> updateAccountDetails(@RequestBody AccountDetailsData request, Authentication authentication) {
        accountDetailsService.updateAccountDetails(authentication.getName(),
                request.getFirstName(),
                request.getSurname());
        return ResponseEntity.ok(GenericResponse.Success("Account details updated successfully"));
    }

}
