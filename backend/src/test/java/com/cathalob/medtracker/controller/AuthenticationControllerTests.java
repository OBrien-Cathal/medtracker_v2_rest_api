package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.payload.request.auth.AccountVerificationRequest;
import com.cathalob.medtracker.payload.request.auth.AuthenticationVerificationRequest;
import com.cathalob.medtracker.payload.request.auth.SignInRequest;
import com.cathalob.medtracker.payload.request.auth.SignUpRequest;
import com.cathalob.medtracker.payload.response.auth.AccountVerificationResponse;
import com.cathalob.medtracker.payload.response.auth.AuthenticationVerificationResponse;
import com.cathalob.medtracker.payload.response.auth.JwtAuthenticationResponse;
import com.cathalob.medtracker.exception.UserAlreadyExistsException;
import com.cathalob.medtracker.exception.UserNotFound;
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
        JwtAuthenticationResponse jwtResponse = JwtAuthenticationResponse.builder().build();
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
                .andExpect(jsonPath("$.currentUserRole", is(jwtResponse.getCurrentUserRole())))
                .andExpect(jsonPath("$.message", is(jwtResponse.getMessage())));
    }

    @Test
    public void givenSignUpRequestForExistingUsername_whenSignUp_thenThrowUserAlreadyExists() throws Exception {
        //given - precondition or setup
        SignUpRequest signUpRequest = aSignUpRequest().build();
        given(authenticationService.signUp(any(SignUpRequest.class)))
                .willThrow(new UserAlreadyExistsException(signUpRequest.getUsername()));

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));
        // then - verify the output
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.errors[0]",
                        is(UserAlreadyExistsException.expandedMessage(signUpRequest.getUsername()))));

    }

    @DisplayName("Test successful sign in ")
    @Test
    public void givenSignInRequest_whenSignIn_thenReturnOk() throws Exception {
        //given - precondition or setup
        SignInRequest signInRequest = SignInRequestBuilder.aSignInRequest().build();
        JwtAuthenticationResponse jwtResponse = JwtAuthenticationResponse.builder().build();
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
                .andExpect(jsonPath("$.currentUserRole", is(jwtResponse.getCurrentUserRole())))
                .andExpect(jsonPath("$.message", is(jwtResponse.getMessage())));
    }

    @DisplayName("Sign in with bad password returns 401")
    @Test
    public void givenSignInRequestWithWrongPassword_whenSignIn_thenReturnUnauthorized() throws Exception {
        //given - precondition or setup
        SignInRequest signInRequest = SignInRequestBuilder.aSignInRequest().build();
        JwtAuthenticationResponse jwtResponse = JwtAuthenticationResponse.builder().build();
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
    public void givenSignInRequestForNonExistentUsername_whenSignIn_thenThrowUserNotFound() throws Exception {
        //given - precondition or setup
        SignInRequest signInRequest = SignInRequestBuilder.aSignInRequest().build();
        given(authenticationService.signIn(any(SignInRequest.class)))
                .willThrow(new UserNotFound(signInRequest.getUsername()));

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)));

        // then - verify the output
        response
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.errors[0]",
                        is(UserNotFound.expandedMessage(signInRequest.getUsername()))));
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

    @Test
    public void givenAccountVerificationRequest_whenVerify_thenReturnAccountVerificationResponseWithAccountExistsTrue()
            throws Exception {
        //given - precondition or setup
        checkAccountExists(true);
    }

    @Test
    public void givenAccountVerificationRequest_whenVerify_thenReturnAccountVerificationResponseWithAccountExistsFalse()
            throws Exception {
        //given - precondition or setup
        checkAccountExists(false);
    }

    private void checkAccountExists(boolean accountExists) throws Exception {
        //given
        AccountVerificationRequest accountVerificationRequest = AccountVerificationRequest.builder().username("user@user.com").build();
        given(authenticationService.checkAccountExists(any(AccountVerificationRequest.class)))
                .willReturn(AccountVerificationResponse.builder().accountExists(accountExists).build());

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/auth/checkaccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountVerificationRequest)));

        // then - verify the output
        response
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountExists",
                        is(accountExists)));
    }

    private String apiHandledExceptionExpectedMessage(String message, Integer code) {
        return "MESSAGE!!";

    }


}
