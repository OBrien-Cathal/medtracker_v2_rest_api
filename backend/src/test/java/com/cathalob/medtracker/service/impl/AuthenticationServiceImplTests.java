package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.UserAuthenticationValidatorException;
import com.cathalob.medtracker.factory.AuthenticationFactory;
import com.cathalob.medtracker.payload.request.auth.AuthenticationVerificationRequest;
import com.cathalob.medtracker.payload.request.auth.SignInRequest;
import com.cathalob.medtracker.payload.request.auth.SignUpRequest;
import com.cathalob.medtracker.payload.response.auth.AuthenticationVerificationResponse;
import com.cathalob.medtracker.payload.response.auth.JwtAuthenticationResponse;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.repository.UserModelRepository;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTests {
    @Mock
    private UserModelRepository userModelRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtServiceImpl jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AccountRegistrationService accountRegistrationService;
    @Mock
    private AuthenticationFactory authenticationFactory;
    @Mock
    private SignInRecordsServiceImpl signInRecordsService;
    @Mock
    private AccountDetailsService accountDetailsService;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;




    @DisplayName("Sign Up request returns jwtAuthenticationResponse without token")
    @Test
    public void givenSignupRequest_whenSignup_thenReturnJwtAuthenticationResponse() {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().build();
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .build();
        given(userModelRepository.findByUsername(signUpRequest.getUsername()))
                .willReturn(Optional.empty());
        given(passwordEncoder.encode(userModel.getPassword())).willReturn(userModel.getPassword());
        given(authenticationFactory.signUpUserModel(userModel.getUsername(), userModel.getPassword()))
                .willReturn(userModel);

        given(userModelRepository.save(userModel)).willReturn(userModel);
        given(accountRegistrationService.registerUserModel(userModel))
                .willReturn(true);
        given(accountDetailsService.addAccountDetails(userModel))
                .willReturn(1L);
        // when - action or the behaviour that we are going test
        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.signUp(signUpRequest);

        // then - verify the output
        verify(userModelRepository, times(1)).save(any(UserModel.class));
        assertThat(jwtAuthenticationResponse).isNotNull();
        assertThat(jwtAuthenticationResponse.getToken()).isNull();
        assertThat(jwtAuthenticationResponse.getCurrentUserRole()).isNull();
        assertThat(jwtAuthenticationResponse.getResponseInfo().isSuccessful()).isTrue();
    }

    @DisplayName("Sign Up existent user does not save a new user but returns success")
    @Test
    public void givenSignUpRequestForExistingUser_whenSignUp_thenThrowUserAlreadyExists() {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withId(1L).build();
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .build();
        given(userModelRepository.findByUsername(signUpRequest.getUsername()))
                .willReturn(Optional.of(userModel));
        given(accountRegistrationService.registerUserModel(userModel))
                .willReturn(false);
        // when - action or the behaviour that we are going test
        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.signUp(signUpRequest);

        // then - verify the output
        assertThat(jwtAuthenticationResponse).isNotNull();
        assertThat(jwtAuthenticationResponse.getToken()).isNull();
        assertThat(jwtAuthenticationResponse.getCurrentUserRole()).isNull();
        assertThat(jwtAuthenticationResponse.getResponseInfo().isSuccessful()).isTrue();
        verify(userModelRepository, never()).save(any(UserModel.class));

    }

    @DisplayName("Successful Sign In request returns jwtAuthenticationResponse")
    @Test
    public void givenSignInRequest_whenSignIn_thenReturnJwtAuthenticationResponse() {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().build();
        SignInRequest signInRequest = SignInRequest.builder()
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .build();
        given(userModelRepository.findByUsername(signInRequest.getUsername()))
                .willReturn(Optional.of(userModel));
        given(accountRegistrationService.isUserRegistrationConfirmed(userModel))
                .willReturn(true);
        String tokenString = "tokenString";
        given(jwtService.generateToken(getUserDetails(userModel))).willReturn(tokenString);

        // when - action or the behaviour that we are going test
        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.signIn(signInRequest);

        // then - verify the output
        assertThat(jwtAuthenticationResponse).isNotNull();
        assertThat(jwtAuthenticationResponse.getToken()).isEqualTo(tokenString);
        assertThat(jwtAuthenticationResponse.getCurrentUserRole()).isEqualTo(userModel.getRole().name());
        assertThat(jwtAuthenticationResponse.getResponseInfo().isSuccessful()).isTrue();
    }

    @DisplayName("Sign In unconfirmed user throws validation exception")
    @Test
    public void givenUnconfirmedUser_whenSignIn_thenThrowValidationError() {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().build();
        SignInRequest signInRequest = SignInRequest.builder()
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .build();
        given(userModelRepository.findByUsername(signInRequest.getUsername()))
                .willReturn(Optional.of(userModel));
        given(accountRegistrationService.isUserRegistrationConfirmed(userModel))
                .willReturn(false);
        // when - action or the behaviour that we are going test
        assertThrows(UserAuthenticationValidatorException.class, () -> authenticationService.signIn(signInRequest));

        // then - verify the output
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }

    @DisplayName("Sign In non existent user throws validation exception")
    @Test
    public void givenSignInRequestForNonExistingUser_whenSignIn_thenThrowUserNotFound() {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().build();
        SignInRequest signInRequest = SignInRequest.builder()
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .build();
        given(userModelRepository.findByUsername(signInRequest.getUsername()))
                .willReturn(Optional.empty());

        // when - action or the behaviour that we are going test
        assertThrows(UserAuthenticationValidatorException.class, () -> authenticationService.signIn(signInRequest));

        // then - verify the output
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }

    @DisplayName("Verify Authentication succeeds for valid jwt token")
    @Test
    public void givenAuthenticationVerificationRequestForValidToken_whenVerifyAuthentication_thenReturnAuthenticationVerificationResponseTrue() {
        //given - precondition or setup
        verifyAuthentication(true);
    }
    @DisplayName("Verify Authentication fails for invalid jwt token")
    @Test
    public void givenAuthenticationVerificationRequestForInValidToken_whenVerifyAuthentication_thenReturnAuthenticationVerificationResponseFalse() {
        //given - precondition or setup
        verifyAuthentication(false);
    }

    private void verifyAuthentication(boolean tokenValid) {
        UserModel userModel = UserModelBuilder.aUserModel().build();
        String tokenString = "tokenString";
        AuthenticationVerificationRequest authenticationVerificationRequest = AuthenticationVerificationRequest.builder()
                .token(tokenString)
                .build();
        given(jwtService.extractUserName(tokenString)).willReturn(userModel.getUsername());
        given(userModelRepository.findByUsername(userModel.getUsername()))
                .willReturn(Optional.of(userModel));
        given(jwtService.isTokenValid(tokenString, getUserDetails(userModel))).willReturn(tokenValid);

        // when - action or the behaviour that we are going test
        AuthenticationVerificationResponse authenticationVerificationResponse = authenticationService.verifyAuthentication(authenticationVerificationRequest);

        // then - verify the output
        assertThat(authenticationVerificationResponse).isNotNull();
        assertThat(authenticationVerificationResponse.isAuthenticated()).isEqualTo(tokenValid);
    }


    private static UserDetails getUserDetails(UserModel user) {
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole()))
                .build();
    }
}