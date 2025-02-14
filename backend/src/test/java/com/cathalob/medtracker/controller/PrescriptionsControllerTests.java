package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.exception.validation.PrescriptionValidatorException;
import com.cathalob.medtracker.mapper.PrescriptionMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.payload.data.PrescriptionDetailsData;
import com.cathalob.medtracker.service.impl.AuthenticationServiceImpl;
import com.cathalob.medtracker.service.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.impl.PrescriptionsService;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.cathalob.medtracker.testdata.MedicationBuilder;
import com.cathalob.medtracker.testdata.PrescriptionScheduleEntryBuilder;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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

import java.time.LocalDateTime;
import java.util.List;

import static com.cathalob.medtracker.testdata.PrescriptionBuilder.aPrescription;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PrescriptionsController.class)
@Import(SecurityConfig.class)
class PrescriptionsControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PrescriptionsService prescriptionsService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceImpl authenticationService;
    @MockBean
    private PrescriptionMapper prescriptionMapper;

    @DisplayName("Get prescriptions returns prescriptions when requested by Practitioner")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenGetPrescriptionsRequestAsPRACTITIONER_whenGetPrescriptions_thenReturnPrescriptions() throws Exception {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().build();

        List<Prescription> prescriptions = List.of(aPrescription().build());
        given(prescriptionMapper.overviews(prescriptions))
                .willReturn(PrescriptionMapper.Overviews(prescriptions));

        given(prescriptionsService.getPatientPrescriptions(userModel.getUsername(), 1L))
                .willReturn(prescriptions);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                get("/api/v1/prescriptions/patient")
                        .param("id", "1"));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));


    }

    @DisplayName("Successful submit of prescription by PRACTITIONER returns success response")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenSubmitPrescriptionRequestAsPRACTITIONER_whenSubmitPrescription_thenReturnSuccessResponse() throws Exception {
        //given - precondition or setup
        PrescriptionDetailsData request = PrescriptionDetailsData.builder()
                .practitionerId(1L)
                .prescriptionScheduleEntries(
                        List.of(PrescriptionScheduleEntryBuilder.aPrescriptionScheduleEntry().build()))
                .beginTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .doseMg(10)
                .medication(MedicationBuilder.aMedication().build())
                .patientId(2L)
                .build();

        Prescription prescription = aPrescription()
                .withPractitioner(UserModelBuilder.aUserModel()).build();
        request.getPrescriptionScheduleEntries()
                .forEach(prescriptionScheduleEntry -> prescriptionScheduleEntry.setPrescription(null));

        Prescription newPrescription = PrescriptionMapper.Prescription(request);

        given(prescriptionMapper.prescription(request))
                .willReturn(newPrescription);

        given(prescriptionsService.submitPrescription(prescription.getPractitioner().getUsername(),
                newPrescription,
                request.getPrescriptionScheduleEntries(),
                request.getPatientId(),
                request.getMedication().getId()))
                .willReturn(1L);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/prescriptions/submit-prescription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(true)))
                .andExpect(jsonPath("$.prescriptionId", is(1)));
    }

    @DisplayName("Failed submit of prescription by PRACTITIONER returns failure response")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenFailureToSubmitPrescriptionRequestAsPRACTITIONER_whenSubmitPrescription_thenReturnFailureResponse() throws Exception {
        //given - precondition or setup
        PrescriptionDetailsData request = PrescriptionDetailsData.builder()
                .practitionerId(1L)
                .prescriptionScheduleEntries(
                        List.of(PrescriptionScheduleEntryBuilder.aPrescriptionScheduleEntry().build()))
                .beginTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .doseMg(10)
                .medication(MedicationBuilder.aMedication().build())
                .patientId(2L)
                .build();

        Prescription prescription = aPrescription()
                .withPractitioner(UserModelBuilder.aUserModel()).build();
        request.getPrescriptionScheduleEntries()
                .forEach(prescriptionScheduleEntry -> prescriptionScheduleEntry.setPrescription(null));

        Prescription newPrescription = PrescriptionMapper.Prescription(request);

        given(prescriptionMapper.prescription(request))
                .willReturn(newPrescription);

        given(prescriptionsService.submitPrescription(prescription.getPractitioner().getUsername(),
                newPrescription,
                request.getPrescriptionScheduleEntries(),
                request.getPatientId(),
                request.getMedication().getId()))
                .willThrow(PrescriptionValidatorException.class);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(
                post("/api/v1/prescriptions/submit-prescription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(false)))
                .andExpect(jsonPath("$.prescriptionId").doesNotExist());
    }
}