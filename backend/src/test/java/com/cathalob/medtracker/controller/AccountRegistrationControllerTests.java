package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.service.impl.AccountRegistrationService;
import com.cathalob.medtracker.service.impl.AuthenticationServiceImpl;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.cathalob.medtracker.service.impl.JwtServiceImpl;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = AccountRegistrationController.class)
class AccountRegistrationControllerTests {
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceImpl authenticationService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountRegistrationService accountRegistrationService;


    @Test
    public void givenUUIDConfirmedByService_whenConfirmRegistration_thenReturnConfirmedString() throws Exception {
        //given - precondition or setup
        UUID reg = UUID.randomUUID();
        System.out.println(reg);
        BDDMockito.given(accountRegistrationService.confirmRegistration(reg, 1L)).willReturn(true);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                get("/api/v1/auth/account-registration/confirm")
                        .param("reg", reg.toString())
                        .param("user-id", "" + 1L));
        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", CoreMatchers.is("Confirmed")));

    }

    @Test
    public void givenUUIDNotConfirmedByService_whenConfirmRegistration_thenReturnNotConfirmedString() throws Exception {
        //given - precondition or setup
        UUID reg = UUID.randomUUID();
        BDDMockito.given(accountRegistrationService.confirmRegistration(reg, 1L)).willReturn(false);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                get("/api/v1/auth/account-registration/confirm")
                        .param("reg", reg.toString())
                        .param("user-id", "" + 1L));
        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", CoreMatchers.is("Confirmation Failed")));

    }
}