package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.exception.validation.medication.MedicationValidationException;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.service.impl.AuthenticationServiceImpl;
import com.cathalob.medtracker.service.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.impl.MedicationsService;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.cathalob.medtracker.testdata.MedicationBuilder.aMedication;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = MedicationsController.class)
class MedicationsControllerTests {
    @MockBean
    private MedicationsService medicationsService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceImpl authenticationService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${api.url}")
    private String baseApiUrl;

    @Test
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenExistingMedications_whenGetMedications_thenReturnListOfMedications() throws Exception {
        //given - precondition or setup
        List<Medication> medicationList = List.of(
                aMedication().withName("Med1").build(),
                aMedication().withName("Med2").build());
        BDDMockito.given(medicationsService.getMedications()).willReturn(medicationList);
        // when - action or the behaviour that we are going test
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(medicationsURL()));
        // then - verify the output
        resultActions.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Validation failure when adding medication returns a failure response")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenExistingMedication_whenAddMedications_thenReturnFailure() throws Exception {
        //given - precondition or setup

        Medication newMed = aMedication().build();

        BDDMockito.given(medicationsService.addMedication(newMed)).willThrow(MedicationValidationException.class);
        // when - action or the behaviour that we are going test
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(medicationsURL() + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMed)));

        // then - verify the output
        resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(false)))
                .andExpect(jsonPath("$.medicationId").doesNotExist());
    }


    @DisplayName("Successful add of medication returns success response")
    @Test
    @WithMockUser(value = "user@user.com", roles = {"PRACTITIONER"})
    public void givenSuccessfulAddMedication_whenAddMedications_thenReturnSuccess() throws Exception {
        //given - precondition or setup

        Medication newMed = aMedication().build();

        BDDMockito.given(medicationsService.addMedication(newMed)).willReturn(1L);
        // when - action or the behaviour that we are going test
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(medicationsURL() + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMed)));

        // then - verify the output
        resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", is(true)))
                .andExpect(jsonPath("$.medicationId", is(1)));
    }

    private String medicationsURL() {
        return baseApiUrl + "/medications";
    }
}