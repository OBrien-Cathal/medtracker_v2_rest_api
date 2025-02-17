package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.payload.response.auth.JwtAuthenticationResponse;

public class JwtAuthenticationResponseBuilder {
    private String username = "user@user.com";
    private String token = "tokenString";
    private String currentUserRole = "USER";


    public JwtAuthenticationResponseBuilder setToken(String token) {
        this.token = token;
        return this;
    }


    public JwtAuthenticationResponseBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public JwtAuthenticationResponseBuilder withCurrentUserRole(String currentUserRole) {
        this.currentUserRole = currentUserRole;
        return this;
    }


    public JwtAuthenticationResponse build() {
        return new JwtAuthenticationResponse(username, token, currentUserRole);
    }

    public static JwtAuthenticationResponseBuilder jwtAuthenticationResponse() {
        return new JwtAuthenticationResponseBuilder();

    }

}