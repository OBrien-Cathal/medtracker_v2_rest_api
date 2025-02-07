package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.payload.data.GraphData;
import com.cathalob.medtracker.payload.request.graph.GraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.graph.PatientGraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.response.TimeSeriesGraphDataResponse;
import com.cathalob.medtracker.service.impl.AuthenticationServiceImpl;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.cathalob.medtracker.service.impl.DoseService;
import com.cathalob.medtracker.service.impl.JwtServiceImpl;
import com.cathalob.medtracker.testdata.UserModelBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DosesController.class)
@Import(SecurityConfig.class)
class DosesControllerTests {
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceImpl authenticationService;
    @MockBean
    private DoseService doseService;

    @Test
    @DisplayName("valid PATIENT request for dose graph data returns success response")
    @WithMockUser(value = "user@user.com", roles = {"PATIENT"})

    public void givenPATIENTValidRequestForGraphData_whenGetDoseGraphData_thenReturnSuccessResponse() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT).build();
        GraphDataForDateRangeRequest request =
                GraphDataForDateRangeRequest.builder()
                        .start(LocalDate.now().plusDays(-1))
                        .end(LocalDate.now().plusDays(1))
                        .build();
        TimeSeriesGraphDataResponse response = TimeSeriesGraphDataResponse.Success(new GraphData());
        System.out.println(response.getResponseInfo());
        given(doseService.getDoseGraphData(userModel.getUsername(), request))
                .willReturn(response);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/doses/graph-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful").isBoolean());
    }

    @Test
    @DisplayName("valid PRACTITIONER request for dose graph data returns success response")
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenPRACTITIONERValidRequestForGraphData_whenGetDoseGraphData_thenReturnSuccessResponse() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT).build();
        PatientGraphDataForDateRangeRequest request = new PatientGraphDataForDateRangeRequest();
        request.setPatientId(1L);
        request.setStart(LocalDate.now().plusDays(-1));
        request.setEnd(LocalDate.now().plusDays(1));

        TimeSeriesGraphDataResponse response = TimeSeriesGraphDataResponse.Success(new GraphData());
        System.out.println(response.getResponseInfo());
        given(doseService.getPatientDoseGraphData(1L, userModel.getUsername(), request))
                .willReturn(response);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/doses/graph-data/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful").isBoolean());
    }

}