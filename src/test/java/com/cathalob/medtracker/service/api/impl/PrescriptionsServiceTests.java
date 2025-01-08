package com.cathalob.medtracker.service.api.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.repository.PrescriptionsRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.cathalob.medtracker.testdata.PrescriptionBuilder.aPrescription;
import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class PrescriptionsServiceTests {
    @Mock
    private PrescriptionsRepository prescriptionsRepository;
    @InjectMocks
    private PrescriptionsService prescriptionsService;

    @DisplayName("Get prescriptions for a PATIENT returns prescriptions for patient role")
    @Test
    public void givenExistingPrescription_whenGetPrescriptions_thenReturnForPatientRole() {
        //given - precondition or setup
        Prescription prescription = aPrescription()
                .withPractitioner(
                        aUserModel()
                                .withId(1L)
                                .withRole(USERROLE.PRACTITIONER))
                .withPatient(aUserModel()
                        .withId(2L)
                        .withRole(USERROLE.PATIENT))
                .build();
        BDDMockito.given(prescriptionsRepository.findByPatient(prescription.getPatient())).willReturn(List.of(prescription));
        // when - action or the behaviour that we are going test
        List<Prescription> prescriptions = prescriptionsService.getPrescriptions(prescription.getPatient());
        // then - verify the output
        assertThat(prescriptions.size()).isEqualTo(1);
        assertThat(prescriptions.get(0).getPatient().getId().equals(prescription.getPatient().getId()));
    }
    @DisplayName("Get prescriptions for a PRACTITIONER returns prescriptions for practitioner role")
    @Test
    public void givenExistingPrescription_whenGetPrescriptions_thenReturnForPRACTITIONERRole() {
        //given - precondition or setup
        Prescription prescription = aPrescription()
                .withPractitioner(
                        aUserModel()
                                .withId(1L)
                                .withRole(USERROLE.PRACTITIONER))
                .withPatient(aUserModel()
                        .withId(2L)
                        .withRole(USERROLE.PATIENT))
                .build();
        BDDMockito.given(prescriptionsRepository.findByPractitioner(prescription.getPractitioner())).willReturn(List.of(prescription));
        // when - action or the behaviour that we are going test
        List<Prescription> prescriptions = prescriptionsService.getPrescriptions(prescription.getPractitioner());
        // then - verify the output
        assertThat(prescriptions.size()).isEqualTo(1);
        assertThat(prescriptions.get(0).getPatient().getId().equals(prescription.getPatient().getId()));
    }

    @DisplayName("Get prescriptions for a USER returns no prescriptions")
    @Test
    public void givenExistingPrescription_whenGetPrescriptions_thenReturnEmptyForUSERRole() {
        //given - precondition or setup
        UserModel userModel = aUserModel().build();
        // when - action or the behaviour that we are going test
        List<Prescription> prescriptions = prescriptionsService.getPrescriptions(userModel);
        // then - verify the output
        assertThat(prescriptions).isEmpty();

    }
}