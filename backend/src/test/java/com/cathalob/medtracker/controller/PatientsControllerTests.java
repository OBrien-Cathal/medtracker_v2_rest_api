package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.exception.validation.PatientRegistrationException;
import com.cathalob.medtracker.mapper.PatientRegistrationMapper;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import com.cathalob.medtracker.payload.request.patient.PatientRegistrationRequest;
import com.cathalob.medtracker.service.impl.AuthenticationServiceImpl;
import com.cathalob.medtracker.service.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.impl.PatientsService;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = PatientsController.class)
class PatientsControllerTests {
    @MockBean
    private PatientsService patientsService;
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
    @MockBean
    private PatientRegistrationMapper patientRegistrationMapper;

    @Value("${api.url}")
    private String baseApiUrl;


    @DisplayName("Successful GetPatientUserModels returns list of UserModels")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenGetPatientRequest_whenGetPatient_thenReturnOnlyPatients() throws Exception {
        //given - precondition or setup
        List<UserModel> users = List.of(
                aUserModel().withRole(USERROLE.PATIENT).withId(1L).build(),
                aUserModel().withRole(USERROLE.PATIENT).withId(2L).build());

        given(patientsService.getPatientUserModelsForPractitioner("user@user.com")).willReturn(users);
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(patientsURL()));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].role", CoreMatchers.is("PATIENT")));
    }


    @DisplayName("Get Patients with un-allowed role (USER) returns UNAUTHORIZED")
    @Test
    @WithMockUser(value = "user@user.com")
    public void givenUNAUTHORIZEDGetPatientRequest_whenGetPatientAsUSERROLE_USER_thenReturnUNAUTHORIZED() throws Exception {
        //given - precondition or setup
        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(patientsURL()));
        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Successful RegisterPatient returns success response")
    @Test
    @WithMockUser(value = "user@user.com")
    public void givenSuccessfulRegisterPatientRequest_whenRegisterPatient_thenReturnSuccess() throws Exception {
        //given - precondition or setup
        PatientRegistrationRequest registrationRequest = PatientRegistrationRequest.builder().practitionerId(2L).build();
        UserModel userModel = aUserModel().withId(1L).build();
        PatientRegistrationRequest.builder().practitionerId(2L).build();
        given(patientsService.registerPatient(userModel.getUsername(), registrationRequest.getPractitionerId()))
                .willReturn(1L);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(post(patientsURL() + "/registrations/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.responseInfo.successful", is(true)))
                .andExpect(jsonPath("$.registrationId", CoreMatchers.is(1)));
    }

    @DisplayName("Failed RegisterPatient returns failure response")
    @Test
    @WithMockUser(value = "user@user.com")
    public void givenFailedRegisterPatientRequest_whenRegisterPatient_thenReturnFailure() throws Exception {
        //given - precondition or setup
        PatientRegistrationRequest registrationRequest = PatientRegistrationRequest.builder().practitionerId(2L).build();
        UserModel userModel = aUserModel().withId(1L).build();
        PatientRegistrationRequest.builder().practitionerId(2L).build();

        given(patientsService.registerPatient(userModel.getUsername(), registrationRequest.getPractitionerId()))
                .willThrow(PatientRegistrationException.class);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(post(patientsURL() + "/registrations/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(false)))
                .andExpect(jsonPath("$.registrationId").doesNotExist());
    }

    private String patientsURL() {
        return baseApiUrl + "/patients";
    }


    @DisplayName("Successful GetPatientRegistrations returns list of PatientRegistrationData")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenExistingPatientRegistrations_whenGetPatientRegistrations_thenReturnPatientRegistrationDataList() throws Exception {
        //given - precondition or setup

        UserModel userModel = aUserModel().withId(1L).build();
        PatientRegistration patientRegistration = new PatientRegistration();
        patientRegistration.setPractitionerUserModel(userModel);

        List<PatientRegistration> registrationList = List.of(patientRegistration);
        PatientRegistrationData data = PatientRegistrationData.builder().practitionerId(userModel.getId()).build();

        given(patientsService.getPatientRegistrations(userModel.getUsername()))
                .willReturn(registrationList);
        given(patientRegistrationMapper.patientRegistrationData(registrationList))
                .willReturn(List.of(data));


        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(patientsURL() + "/registrations"));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].practitionerId", CoreMatchers.is(1)));
    }

}