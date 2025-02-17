package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.payload.request.auth.AuthenticationVerificationRequest;
import com.cathalob.medtracker.payload.request.auth.SignInRequest;
import com.cathalob.medtracker.payload.request.auth.SignUpRequest;
import com.cathalob.medtracker.payload.response.auth.AuthenticationVerificationResponse;
import com.cathalob.medtracker.payload.response.auth.JwtAuthenticationResponse;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import com.cathalob.medtracker.service.impl.AuthenticationServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationServiceImpl authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.signUp(request));
        } catch (UnexpectedRollbackException e) {
            return ResponseEntity.ok(new JwtAuthenticationResponse(ResponseInfo.Failed(List.of("Too many sign up attempts"))));
        }

    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody @Valid SignInRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthenticationVerificationResponse> verify(@RequestBody AuthenticationVerificationRequest request) {
        return ResponseEntity.ok(authenticationService.verifyAuthentication(request));
    }

}



