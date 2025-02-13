package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.AccountDetails;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.service.impl.*;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = AccountDetailsController.class)
class AccountDetailsControllerTests {
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceImpl authenticationService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountDetailsService accountDetailsService;

    @DisplayName("(Stub implementation) Request for details returns account details object")
    @Test
    @WithMockUser(value = "user@user.com")
    public void givenExistingAccountDetails_whenDetailsRequested_thenReturnAccountDetails() throws Exception {
        //given - precondition or setup
        AccountDetails accountDetails = new AccountDetails();
        UserModel user = UserModelBuilder.aUserModel().withId(3L).build();
        accountDetails.setFirstName("TEST");
        BDDMockito.given(accountDetailsService.getDetails(user.getUsername()))
                .willReturn(accountDetails);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                get("/api/v1/account-details")
                        .contentType(MediaType.APPLICATION_JSON));
        // then - verify the output
        System.out.println(usersResponse);
        usersResponse
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.firstName", CoreMatchers.is(accountDetails.getFirstName())));
    }

    @DisplayName("(Stub implementation) Update account details returns account details id")
    @Test
    @WithMockUser(value = "user@user.com")
    public void givenServiceUpdatesAccountDetails_whenAccountDetailsPosted_thenReturnAccountDetailsID() throws Exception {
        //given - precondition or setup
        UserModel user = UserModelBuilder.aUserModel().withId(3L).build();

        BDDMockito.given(accountDetailsService.updateAccountDetails(user.getUsername()))
                .willReturn(1L);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/account-details")
                        .contentType(MediaType.APPLICATION_JSON));
        // then - verify the output

        usersResponse
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$", CoreMatchers.is(1)));
    }


}