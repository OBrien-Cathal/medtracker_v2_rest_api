package com.cathalob.medtracker.service;

import com.cathalob.medtracker.payload.request.auth.SignInRequest;
import com.cathalob.medtracker.payload.request.auth.SignUpRequest;
import com.cathalob.medtracker.payload.response.auth.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse signUp(SignUpRequest request);

    JwtAuthenticationResponse signIn(SignInRequest request);
}
