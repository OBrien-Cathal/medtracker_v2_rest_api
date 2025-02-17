package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;

import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.service.impl.AuthenticationServiceImpl;
import com.cathalob.medtracker.service.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = UsersController.class)
class UserControllerApiTests {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceImpl authenticationService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Value("${api.url}" + "/users")
    private String controllerEndpoint;

    @Test
    @WithMockUser("user@user.com")
    public void givenGetUserModelsRequest_when_then() throws Exception {
        //given - precondition or setup
        List<UserModel> users = List.of(aUserModel().withId(1L).build(), aUserModel().withId(2L).build());

        BDDMockito.given(userService.getUserModels()).willReturn(users);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(controllerEndpoint));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @DisplayName("Get Practitioners only returns UserModels with USERROLE PRACTITIONER")
    @Test
    @WithMockUser("user@user.com")
    public void givenGetPractitionersRequest_whenGetPractitioners_thenReturnOnlyPractitioners() throws Exception {
        //given - precondition or setup
        List<UserModel> users = List.of(
                aUserModel().withRole(USERROLE.PRACTITIONER).withId(1L).build(),
                aUserModel().withRole(USERROLE.PRACTITIONER).withId(2L).build());

        BDDMockito.given(userService.getPractitionerUserModels()).willReturn(users);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(controllerEndpoint + "/practitioners"));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].role",CoreMatchers.is("PRACTITIONER")) );
    }
}