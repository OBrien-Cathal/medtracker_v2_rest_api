package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.exception.validation.dose.DailyDoseDataException;
import com.cathalob.medtracker.exception.validation.dose.DoseGraphDataException;
import com.cathalob.medtracker.mapper.DoseMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.payload.data.DailyDoseData;
import com.cathalob.medtracker.payload.request.graph.GraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.graph.PatientGraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.patient.AddDailyDoseDataRequest;

import com.cathalob.medtracker.service.impl.AuthenticationServiceImpl;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.cathalob.medtracker.service.impl.DoseService;
import com.cathalob.medtracker.service.impl.JwtServiceImpl;
import com.cathalob.medtracker.testdata.*;
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

import static com.cathalob.medtracker.testdata.DoseBuilder.aDose;
import static org.hamcrest.CoreMatchers.is;
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
    @MockBean
    private DoseMapper doseMapper;


    @Test
    @DisplayName("valid add daily dose data request return success response")
    @WithMockUser(value = "user@user.com", roles = {"PATIENT"})

    public void givenValidAddDailyDoseRequest_whenAddDaily_thenReturnSuccessResponse() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT).build();
        AddDailyDoseDataRequest request = AddDailyDoseDataRequest.builder()
                .dailyDoseData(DailyDoseData.builder()
                        .prescriptionScheduleEntryId(1L)
                        .taken(true)
                        .build())
                .date(LocalDate.now())
                .build();

        Dose newDose = DoseMapper.Dose(request);
        given(doseMapper.dose(request)).willReturn(newDose);

        given(doseService.addDailyDoseData(
                userModel.getUsername(),
                newDose,
                1L,
                request.getDate()))
                .willReturn(1L);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/doses/add-daily-dose-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(true)))
                .andExpect(jsonPath("$.doseId", is(1)));
    }


    @Test
    @DisplayName("Invalid add daily dose data request return success response")
    @WithMockUser(value = "user@user.com", roles = {"PATIENT"})

    public void givenValidUpdateDailyDoseRequest_whenUpdateDaily_thenReturnSuccessResponse() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT).build();
        AddDailyDoseDataRequest request = AddDailyDoseDataRequest.builder()
                .dailyDoseData(DailyDoseData.builder()
                        .prescriptionScheduleEntryId(1L)
                        .taken(true)
                        .build())
                .date(LocalDate.now())
                .build();
        Dose newDose = DoseMapper.Dose(request);
        given(doseMapper.dose(request)).willReturn(newDose);

        given(doseService.addDailyDoseData(
                userModel.getUsername(),
                newDose,
                1L,
                request.getDate()))
                .willThrow(DailyDoseDataException.class);


        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/doses/add-daily-dose-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(false)))
                .andExpect(jsonPath("$.doseId").isEmpty());
    }


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
        TreeMap<LocalDate, List<Dose>> responseMap = new TreeMap<>();

        responseMap.put(request.getStart(), List.of(aDose()
                .withPrescriptionScheduleEntryBuilder(PrescriptionScheduleEntryBuilder.aPrescriptionScheduleEntry()
                        .with(PrescriptionBuilder.aPrescription()
                                .with(MedicationBuilder.aMedication()
                                        .withId(1L))))
                .build()));

        given(doseService.getDoseGraphData(userModel.getUsername(), request.getStart(),
                request.getEnd()))
                .willReturn(responseMap);
        given(doseMapper.getDoseGraphData(responseMap)).willReturn(DoseMapper.GetDoseGraphData(responseMap));


        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/doses/graph-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(true)))
                .andExpect(jsonPath("$.graphData").isNotEmpty())
                .andExpect(jsonPath("$.graphData.columnNames[1]", is("Medication (Wakeup)")));
    }

    @Test
    @DisplayName("valid PRACTITIONER request for dose graph data returns success response")
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenPRACTITIONERValidRequestForGraphData_whenGetDoseGraphData_thenReturnSuccessResponse() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PRACTITIONER).build();
        PatientGraphDataForDateRangeRequest request = new PatientGraphDataForDateRangeRequest();
        request.setPatientId(1L);
        request.setStart(LocalDate.now().plusDays(-1));
        request.setEnd(LocalDate.now().plusDays(1));

        TreeMap<LocalDate, List<Dose>> responseMap = new TreeMap<>();

        responseMap.put(request.getStart(), List.of(aDose()
                .withPrescriptionScheduleEntryBuilder(PrescriptionScheduleEntryBuilder.aPrescriptionScheduleEntry()
                        .with(PrescriptionBuilder.aPrescription()
                                .with(MedicationBuilder.aMedication()
                                        .withId(1L))))
                .build()));

        given(doseService.getPatientDoseGraphData(request.getPatientId(), userModel.getUsername(), request.getStart(),
                request.getEnd()))
                .willReturn(responseMap);
        given(doseMapper.getDoseGraphData(responseMap)).willReturn(DoseMapper.GetDoseGraphData(responseMap));
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/doses/graph-data/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));


        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(true)))
                .andExpect(jsonPath("$.graphData").isNotEmpty())
                .andExpect(jsonPath("$.graphData.columnNames[1]", is("Medication (Wakeup)")));
    }


    @Test
    @DisplayName("Invalid PRACTITIONER request for dose graph data returns failure response")
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenPRACTITIONERInvalidRequestForGraphData_whenGetDoseGraphData_thenReturnFailureResponse() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PRACTITIONER).build();
        PatientGraphDataForDateRangeRequest request = new PatientGraphDataForDateRangeRequest();
        request.setPatientId(1L);
        request.setStart(LocalDate.now().plusDays(-1));
        request.setEnd(LocalDate.now().plusDays(1));

        given(doseService.getPatientDoseGraphData(request.getPatientId(), userModel.getUsername(), request.getStart(),
                request.getEnd()))
                .willThrow(DoseGraphDataException.class);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/doses/graph-data/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(false)))
                .andExpect(jsonPath("$.graphData").isEmpty());

    }

    @Test
    @DisplayName("Invalid PATIENT request for dose graph data returns failure response")
    @WithMockUser(value = "user@user.com", roles = {"PATIENT"})
    public void givenPATIENTInvalidRequestForGraphData_whenGetDoseGraphData_thenReturnFailureResponse() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT).build();
        GraphDataForDateRangeRequest request =
                GraphDataForDateRangeRequest.builder()
                        .start(LocalDate.now().plusDays(-1))
                        .end(LocalDate.now().plusDays(1))
                        .build();

        given(doseService.getDoseGraphData(userModel.getUsername(), request.getStart(),
                request.getEnd()))
                .willThrow(DoseGraphDataException.class);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/doses/graph-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(false)))
                .andExpect(jsonPath("$.graphData").isEmpty());

    }

}