package com.cathalob.medtracker.service.api.impl;

import com.cathalob.medtracker.dao.request.SignInRequest;
import com.cathalob.medtracker.dao.request.SignUpRequest;
import com.cathalob.medtracker.dao.response.JwtAuthenticationResponse;
import com.cathalob.medtracker.err.UserAlreadyExistsException;
import com.cathalob.medtracker.err.UserNotFound;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.repository.UserModelRepository;
import com.cathalob.medtracker.service.api.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceApi implements com.cathalob.medtracker.service.api.AuthenticationServiceApi {
    private final UserModelRepository userModelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        if (userModelRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        var user = UserModel.builder().username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(USERROLE.USER).build();
        userModelRepository.save(user);
        UserDetails userDetails = User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole()))
                .build();
        var jwt = jwtService.generateToken(userDetails);
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .message("success")
                .build();
    }

    @Override
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        String username = request.getUsername();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword()));
        var user = userModelRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound(username));
        UserDetails userDetails = User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole()))
                .build();
        var jwt = jwtService.generateToken(userDetails);
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .message("success")
                .build();
    }

}
