package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.repository.UserModelRepository;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class UserServiceImplTests {
    @Mock
    private UserModelRepository userModelRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private UserModel userModel;

    @BeforeEach
    public void setUp() {
        this.userModel = aUserModel().withId(1L).build();
    }


    @DisplayName("Get all UserModels")
    @Test
    public void givenGetUsersRequest_whenGetUserModels_thenReturnAllUserModels() {
        //given - precondition or setup
        given(userModelRepository
                .findAll()).willReturn(List.of(userModel, aUserModel().withId(2L).withUsername("username2").build()));

        // when - action or the behaviour that we are going test
        List<UserModel> userModels = userService.getUserModels();

        // then
        assertThat(userModels).isNotNull();
        assertThat(userModels.size()).isEqualTo(2);
    }

    @Disabled
    @DisplayName("Failed password change request - unimplemented")
    @Test
    public void givenUserRequest_whenSubmitPasswordChangeRequest_thenReturnFalse() {
        //given - No setup

        // when - action or the behaviour that we are going test
        boolean requestStatus = userService.submitPasswordChangeRequest();
        // then - verify the output
        assertThat(requestStatus).isFalse();
    }


}