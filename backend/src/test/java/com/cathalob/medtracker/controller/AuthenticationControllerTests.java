package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.payload.request.auth.AuthenticationVerificationRequest;
import com.cathalob.medtracker.payload.request.auth.SignInRequest;
import com.cathalob.medtracker.payload.request.auth.SignUpRequest;
import com.cathalob.medtracker.payload.response.auth.AuthenticationVerificationResponse;
import com.cathalob.medtracker.payload.response.auth.JwtAuthenticationResponse;
import com.cathalob.medtracker.service.impl.AuthenticationServiceImpl;
import com.cathalob.medtracker.service.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.cathalob.medtracker.testdata.SignInRequestBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.UnexpectedRollbackException;


import static com.cathalob.medtracker.testdata.SignUpRequestBuilder.aSignUpRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = AuthenticationController.class)
class AuthenticationControllerTests {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceImpl authenticationService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenSignUpRequest_whenSignUp_thenReturnOk() throws Exception {
        //given - precondition or setup
        SignUpRequest signUpRequest = aSignUpRequest().build();
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse();
        given(authenticationService.signUp(any(SignUpRequest.class)))
                .willReturn(jwtResponse);

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        // then - verify the output
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(jwtResponse.getToken())))
                .andExpect(jsonPath("$.currentUserRole", is(jwtResponse.getCurrentUserRole())));
    }

    @Test
    public void givenSignUpRequestForExistingUsername_whenSignUp_thenThrowUnexpectedRollbackException() throws Exception {
        //given - precondition or setup
        SignUpRequest signUpRequest = aSignUpRequest().build();
        given(authenticationService.signUp(any(SignUpRequest.class)))
                .willThrow(new UnexpectedRollbackException("test"));

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));
        // then - verify the output
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(false)));


    }

    @DisplayName("Test successful sign in ")
    @Test
    public void givenSignInRequest_whenSignIn_thenReturnOk() throws Exception {
        //given - precondition or setup
        SignInRequest signInRequest = SignInRequestBuilder.aSignInRequest().build();
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse();
        given(authenticationService.signIn(any(SignInRequest.class)))
                .willReturn(jwtResponse);

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)));

        // then - verify the output
        response
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(jwtResponse.getToken())))
                .andExpect(jsonPath("$.currentUserRole", is(jwtResponse.getCurrentUserRole())));
    }

    @DisplayName("Sign in with bad password returns 401")
    @Test
    public void givenSignInRequestWithWrongPassword_whenSignIn_thenReturnUnauthorized() throws Exception {
        //given - precondition or setup
        SignInRequest signInRequest = SignInRequestBuilder.aSignInRequest().build();
        given(authenticationService.signIn(any(SignInRequest.class)))
                .willThrow(BadCredentialsException.class);

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)));

        // then - verify the output
        response
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void givenAuthenticationVerificationRequest_whenVerify_thenReturnAuthenticationVerificationResponseWithAuthenticatedTrue()
            throws Exception {
        //given - precondition or setup
        verifyAuthenticated(true);
    }

    @Test
    public void givenExpiredAuthenticationVerificationRequest_whenVerify_thenReturnAuthenticationVerificationResponseWithAuthenticatedFalse()
            throws Exception {
        //given - precondition or setup
        verifyAuthenticated(false);
    }

    private void verifyAuthenticated(boolean authenticated) throws Exception {
//        given
        AuthenticationVerificationRequest authenticationVerificationRequest =
                AuthenticationVerificationRequest.builder().token("aTokenString").build();
        given(authenticationService.verifyAuthentication(any(AuthenticationVerificationRequest.class)))
                .willReturn(AuthenticationVerificationResponse.builder().authenticated(authenticated).build());

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationVerificationRequest)));

        // then - verify the output
        response
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated",
                        is(authenticated)));
    }
}
