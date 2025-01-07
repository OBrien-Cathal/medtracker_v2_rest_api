package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.service.api.impl.AuthenticationServiceApi;
import com.cathalob.medtracker.service.api.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.api.impl.MedicationsService;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.cathalob.medtracker.testdata.MedicationBuilder;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.cathalob.medtracker.testdata.MedicationBuilder.aMedication;
import static org.junit.jupiter.api.Assertions.*;

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
    private AuthenticationServiceApi authenticationServiceApi;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

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

    private String medicationsURL() {
        return baseApiUrl + "/medications";
    }
}