package com.cathalob.medtracker.service.web;

import com.cathalob.medtracker.err.UserAlreadyExistsException;
import com.cathalob.medtracker.err.UserNotFound;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.repository.UserModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceApiTests {
    @Mock
    private UserModelRepository userModelRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthenticationServiceWeb authenticationServiceWeb;
    private UserModel userModel;

    @BeforeEach
    public void setUp() {
        this.userModel = aUserModel().withId(1L).build();
    }

    @DisplayName("Find UserModel for email/username")
    @Test
    public void givenRegisteredUsername_whenFindByLogin_thenReturnUserModel() {
        //given - precondition or setup
        given(userModelRepository
                .findByUsername(userModel.getUsername())).willReturn(Optional.of(userModel));
        // when - action or the behaviour that we are going test
        UserModel registeredUserModel = authenticationServiceWeb.findByLogin(userModel.getUsername());
        // then - verify the output
        assertThat(registeredUserModel).isNotNull();
    }

    @DisplayName("Fail to find UserModel for email/username")
    @Test
    public void givenUnregisteredUsername_whenFindByLogin_thenThrowUserNotFound() {
        //given - precondition or setup
        given(userModelRepository
                .findByUsername(userModel.getUsername())).willReturn(Optional.empty());

        // when/then
        assertThrows(UserNotFound.class, () -> authenticationServiceWeb.findByLogin(userModel.getUsername()));
    }

    @DisplayName("Successful User model Registration")
    @Test
    public void givenUserModel_whenRegister_thenUserModelIsRegistered() {
        //given - precondition or setup
        given(userModelRepository
                .findByUsername(userModel.getUsername())).willReturn(Optional.empty());
        given(userModelRepository.save(userModel)).willReturn(userModel);
        given(passwordEncoder.encode(userModel.getPassword())).willReturn("abc");

        // when - action or the behaviour that we are going test
        UserModel registeredUserModel = authenticationServiceWeb.register(userModel);
        // then - verify the output
        assertThat(registeredUserModel).isNotNull();

    }

    @DisplayName("Failed User model Registration")
    @Test
    public void givenExistingUserModelEmail_whenRegister_thenUserModelIsNOTRegistered() {
        //given - precondition or setup
        given(userModelRepository
                .findByUsername(userModel.getUsername())).willReturn(Optional.of(userModel));

        // when - action or the behaviour that we are going test
        assertThrows(UserAlreadyExistsException.class, () -> authenticationServiceWeb.register(userModel));

        // then
        verify(userModelRepository, never()).save(any(UserModel.class));
    }
}