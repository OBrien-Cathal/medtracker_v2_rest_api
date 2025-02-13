package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.model.AccountDetails;
import com.cathalob.medtracker.service.impl.AccountDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/account-details")
@RequiredArgsConstructor

public class AccountDetailsController {
    private final AccountDetailsService accountDetailsService;

    @GetMapping()
    public ResponseEntity<AccountDetails> getAccountDetails(Authentication authentication) {
        AccountDetails details = accountDetailsService.getDetails(authentication.getName());
        System.out.println(details);
        return ResponseEntity.ok(details);
    }

    @PostMapping()
    public ResponseEntity<Long> updateAccountDetails(Authentication authentication) {
        return ResponseEntity.ok(accountDetailsService.updateAccountDetails(authentication.getName()));
    }

}
