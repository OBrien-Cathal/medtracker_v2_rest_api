package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.exception.validation.bloodpressure.AddBloodPressureDailyDataException;
import com.cathalob.medtracker.exception.validation.bloodpressure.BloodPressureDailyDataExceptionData;
import com.cathalob.medtracker.mapper.BloodPressureMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.payload.data.BloodPressureData;
import com.cathalob.medtracker.payload.data.GraphData;
import com.cathalob.medtracker.payload.request.graph.GraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.graph.PatientGraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.patient.AddDatedBloodPressureReadingRequest;
import com.cathalob.medtracker.payload.request.patient.DatedBloodPressureDataRequest;
import com.cathalob.medtracker.service.impl.*;
import com.cathalob.medtracker.testdata.BloodPressureReadingBuilder;
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
import java.util.List;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BloodPressureController.class)
@Import(SecurityConfig.class)
class BloodPressureControllerTests {
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
    private BloodPressureDataService bloodPressureDataService;
    @MockBean
    private BloodPressureMapper bloodPressureMapper;

    @DisplayName("valid request for BP daily data returns success response")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PATIENT"})
    public void givenValidDailyDataRequest_whenGetBloodPressureData_thenReturnSuccess() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT).build();

        DatedBloodPressureDataRequest request = DatedBloodPressureDataRequest.builder().date(LocalDate.now()).build();

        List<BloodPressureReading> readings = List.of(BloodPressureReadingBuilder.aBloodPressureReading().build());
        List<BloodPressureData> data = BloodPressureMapper.BloodPressureDataList(readings);

        given(bloodPressureMapper.bloodPressureDataList(readings)).willReturn(data);
        given(bloodPressureDataService.getBloodPressureData(userModel.getUsername(), request.getDate()))
                .willReturn(readings);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/blood-pressure/blood-pressure-daily-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(true)))
                .andExpect(jsonPath("$.readings[0].systole", is(120)));
    }

    @DisplayName("Invalid request for BP daily data returns Failure response")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PATIENT"})
    public void givenInvalidDailyDataRequest_whenGetBloodPressureData_thenReturnFailure() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT).build();

        DatedBloodPressureDataRequest request = DatedBloodPressureDataRequest.builder().date(LocalDate.now()).build();

        given(bloodPressureDataService.getBloodPressureData(userModel.getUsername(), request.getDate()))
                .willThrow(BloodPressureDailyDataExceptionData.class);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/blood-pressure/blood-pressure-daily-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(false)))
                .andExpect(jsonPath("$.readings").isEmpty());
    }

    @DisplayName("Valid add BP daily data returns success response")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PATIENT"})
    public void givenValidAddDailyDataRequest_whenAddBloodPressureDailyData_thenReturnSuccess() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT).build();

        AddDatedBloodPressureReadingRequest request = AddDatedBloodPressureReadingRequest.builder()
                .data(BloodPressureData.builder().build())
                .date(LocalDate.now()).build();

        BloodPressureReading newReading = BloodPressureMapper.ToBloodPressureReading(request);

        given(bloodPressureMapper.toBloodPressureReading(request)).willReturn(newReading);
        given(bloodPressureDataService.addBloodPressureReading( newReading,request.getDate(), userModel.getUsername()))
                .willReturn(1L);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/blood-pressure/add-blood-pressure-daily-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(true)))
                .andExpect(jsonPath("$.bloodPressureReadingId", is(1)));
    }


    @DisplayName("Invalid add BP daily data returns failure response")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PATIENT"})
    public void givenInvalidAddDailyDataRequest_whenAddBloodPressureDailyData_thenReturnFailure() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT).build();

        AddDatedBloodPressureReadingRequest request = AddDatedBloodPressureReadingRequest.builder()
                .data(BloodPressureData.builder().build())
                .date(LocalDate.now()).build();

        BloodPressureReading newReading = BloodPressureMapper.ToBloodPressureReading(request);

        given(bloodPressureMapper.toBloodPressureReading(request)).willReturn(newReading);
        given(bloodPressureDataService.addBloodPressureReading( newReading,request.getDate(), userModel.getUsername()))
                .willThrow(AddBloodPressureDailyDataException.class);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/blood-pressure/add-blood-pressure-daily-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(false)))
                .andExpect(jsonPath("$.bloodPressureReadingId").isEmpty());
    }



    @DisplayName("valid PATIENT request for BP graph data returns success response")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PATIENT"})
    public void givenPATIENTValidGraphDataRequest_whenGetSystoleGraphData_thenReturnSuccess2() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT).build();

        GraphDataForDateRangeRequest request = GraphDataForDateRangeRequest.builder()
                .start(LocalDate.now().plusDays(-1))
                .end(LocalDate.now().plusDays(1))
                .build();


        TreeMap<LocalDate, List<BloodPressureReading>> bpReadingMap = new TreeMap<>();
        given(bloodPressureMapper.getSystoleGraphData(bpReadingMap))
                .willReturn(new GraphData());
        given(bloodPressureDataService.getBloodPressureReadingsForDateRange(userModel.getUsername(), request.getStart(), request.getEnd()))
                .willReturn(bpReadingMap);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/blood-pressure/systole-graph-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(true)))
                .andExpect(jsonPath("$.graphData").isNotEmpty());
    }

    @DisplayName("valid PRACTITIONER request for BP graph data returns success response")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenPRACTITIONERGraphDataRequest_whenGetPatientSystoleGraphData_thenReturnResponse2() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PRACTITIONER).build();

        PatientGraphDataForDateRangeRequest request = new PatientGraphDataForDateRangeRequest();
        request.setPatientId(1L);
        request.setStart(LocalDate.now().plusDays(-1));
        request.setEnd(LocalDate.now().plusDays(1));

        TreeMap<LocalDate, List<BloodPressureReading>> bpReadingMap = new TreeMap<>();
        given(bloodPressureMapper.getSystoleGraphData(bpReadingMap))
                .willReturn(new GraphData());
        given(bloodPressureDataService.getPatientBloodPressureReadingsForDateRange(request.getPatientId(),
                userModel.getUsername(),
                request.getStart(),
                request.getEnd()))
                .willReturn(bpReadingMap);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/blood-pressure/systole-graph-data/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(true)))
                .andExpect(jsonPath("$.graphData").isNotEmpty());
    }

}