package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.payload.response.Response;
import com.cathalob.medtracker.service.api.impl.AuthenticationServiceApi;
import com.cathalob.medtracker.service.api.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.api.impl.PrescriptionsService;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.cathalob.medtracker.testdata.PrescriptionBuilder;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PrescriptionsController.class)
@Import(SecurityConfig.class)
class PrescriptionsControllerTests {
    @MockBean
    private PrescriptionsService prescriptionsService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceApi authenticationServiceApi;

    @DisplayName("Get prescriptions returns prescriptions when requested by Practitioner")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenGetPrescriptionsRequestAsPRACTITIONER_whenGetPrescriptions_thenReturnPrescriptions() throws Exception {
        //given - precondition or setup
        Response response = new Response(
                true,
                "Stub");
        UserModel userModel = UserModelBuilder.aUserModel().build();

        BDDMockito.given(prescriptionsService.getPrescriptions(userModel.getUsername()))
                .willReturn(List.of(PrescriptionBuilder.aPrescription().build()));
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get("/api/v1/prescriptions"));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));


    }
}