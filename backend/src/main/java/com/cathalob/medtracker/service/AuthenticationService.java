package com.cathalob.medtracker.service;

import com.cathalob.medtracker.payload.request.SignInRequest;
import com.cathalob.medtracker.payload.request.SignUpRequest;
import com.cathalob.medtracker.payload.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse signUp(SignUpRequest request);

    JwtAuthenticationResponse signIn(SignInRequest request);
}
