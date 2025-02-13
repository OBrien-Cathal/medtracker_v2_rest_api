package com.cathalob.medtracker.payload.response.auth;

import com.cathalob.medtracker.payload.response.generic.Response;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import lombok.*;

@Data

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor

public class JwtAuthenticationResponse extends Response {

    public JwtAuthenticationResponse(ResponseInfo responseInfo, String token, String currentUserRole) {
        super(responseInfo);
        this.token = token;
        this.currentUserRole = currentUserRole;
    }

    public JwtAuthenticationResponse(ResponseInfo responseInfo) {
        super(responseInfo);
    }

    private String token;
    private String currentUserRole;

}