package com.cathalob.medtracker.service.api;

import com.cathalob.medtracker.dao.request.SignInRequest;
import com.cathalob.medtracker.dao.request.SignUpRequest;
import com.cathalob.medtracker.dao.response.JwtAuthenticationResponse;

public interface AuthenticationServiceApi {
    JwtAuthenticationResponse signUp(SignUpRequest request);

    JwtAuthenticationResponse signIn(SignInRequest request);
}
