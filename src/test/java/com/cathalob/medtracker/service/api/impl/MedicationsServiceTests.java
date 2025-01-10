package com.cathalob.medtracker.service.api.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.payload.response.Response;
import com.cathalob.medtracker.repository.MedicationRepository;

import com.cathalob.medtracker.service.impl.MedicationsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.cathalob.medtracker.testdata.MedicationBuilder.aMedication;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class MedicationsServiceTests {
    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private MedicationsService medicationsService;

    @DisplayName("Successful add medication")
    @Test
    public void givenNewMedication_whenAddMedication_thenReturnSuccessfulResponse() {
        //given - precondition or setup
        Medication medication = aMedication().withId(1L).build();
        given(medicationRepository.save(medication)).willReturn(medication);
        // when - action or the behaviour that we are going test
        Response requestResponse = medicationsService.addMedication(medication);
        // then - verify the output
        Assertions.assertThat(requestResponse.isSuccessful()).isTrue();
    }

    @DisplayName("Fail validation: Medication with name already exists ")
    @Test
    public void givenExistingMedication_whenAddMedication_thenReturnFailureResponse() {
        //given - precondition or setup
        Medication medication = aMedication().withId(1L).build();
        given(medicationRepository.findByName(medication.getName())).willReturn(List.of(medication));

        // when - action or the behaviour that we are going test
        Response requestResponse = medicationsService.addMedication(medication);
        // then - verify the output
        Assertions.assertThat(requestResponse.isSuccessful()).isFalse();
        Assertions.assertThat(requestResponse.getErrors()).isNotEmpty();
    }

}