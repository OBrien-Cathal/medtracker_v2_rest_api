package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.UserAuthenticationValidatorException;
import com.cathalob.medtracker.factory.AuthenticationFactory;
import com.cathalob.medtracker.payload.request.auth.AuthenticationVerificationRequest;
import com.cathalob.medtracker.payload.request.auth.SignInRequest;
import com.cathalob.medtracker.payload.request.auth.SignUpRequest;
import com.cathalob.medtracker.payload.response.auth.AuthenticationVerificationResponse;
import com.cathalob.medtracker.payload.response.auth.JwtAuthenticationResponse;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import com.cathalob.medtracker.repository.UserModelRepository;
import com.cathalob.medtracker.service.JwtService;
import com.cathalob.medtracker.service.SignInRecordsService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationServiceImpl implements com.cathalob.medtracker.service.AuthenticationService {
    private final UserModelRepository userModelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AccountRegistrationService accountRegistrationService;
    private final AuthenticationFactory authenticationFactory;
    private final SignInRecordsService signInRecordsService;

    @Override
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        String lowerCaseUsername = request.getUsername().toLowerCase();
//        LocalDateTime signupTimestamp = LocalDateTime.now();
//        System.out.println("Signup Attempt: " + signupTimestamp);
        UserModel user = userModelRepository.findByUsername(lowerCaseUsername)
                .orElseGet(() -> authenticationFactory.signUpUserModel(
                        lowerCaseUsername,
                        passwordEncoder.encode(request.getPassword())));

        boolean userAlreadyExists = user.getId() == null;
        try {
            if (userAlreadyExists) userModelRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            log.info(exception.getMessage());
            return new JwtAuthenticationResponse(ResponseInfo.Success("A registration confirmation was sent to the submitted email address"));
        }
        accountRegistrationService.registerUserModel(user);
        return new JwtAuthenticationResponse(ResponseInfo.Success("A registration confirmation was sent to the submitted email address"));
    }

    @Override
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        String username = request.getUsername().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword()));

        var user = userModelRepository.findByUsername(username).orElse(null);

        if (user == null || !accountRegistrationService.isUserRegistrationConfirmed(user))
            throw new UserAuthenticationValidatorException(List.of("No confirmed registered user exists for submitted email and password"));

        signInRecordsService.addSignInRecord(user);

        UserDetails userDetails = getUserDetails(user);
        var jwt = jwtService.generateToken(userDetails);

        return new JwtAuthenticationResponse(ResponseInfo.Success("Sign in successful"), user.getUsername(), jwt, user.getRole().name());
    }

    private static UserDetails getUserDetails(UserModel user) {
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole()))
                .build();
    }

    public AuthenticationVerificationResponse verifyAuthentication(AuthenticationVerificationRequest request) {
        String token = request.getToken();
        boolean tokenValid = false;
        if (token.isEmpty()) return getAuthenticationVerificationResponse(tokenValid);
        try {
            String username = jwtService.extractUserName(token);
            Optional<UserModel> maybeUserModel = userModelRepository.findByUsername(username);
            if (maybeUserModel.isEmpty()) {
                return getAuthenticationVerificationResponse(tokenValid);
            }
            tokenValid = jwtService.isTokenValid(token,
                    getUserDetails(maybeUserModel.get()));

        } catch (ExpiredJwtException expiredJwtException) {
            return getAuthenticationVerificationResponse(tokenValid);
        }
        return getAuthenticationVerificationResponse(tokenValid);
    }

    private static AuthenticationVerificationResponse getAuthenticationVerificationResponse(boolean tokenValid) {
        return AuthenticationVerificationResponse.builder().authenticated(tokenValid).build();
    }
}
