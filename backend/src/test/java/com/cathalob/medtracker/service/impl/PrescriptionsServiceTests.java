package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.payload.data.PrescriptionOverviewData;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.repository.PrescriptionsRepository;

import com.cathalob.medtracker.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static com.cathalob.medtracker.testdata.PrescriptionBuilder.aPrescription;
import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class PrescriptionsServiceTests {
    @Mock
    private PrescriptionsRepository prescriptionsRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private PrescriptionsService prescriptionsService;
    @Mock
    private PatientRegistrationRepository patientRegistrationRepository;

    @DisplayName("(GetPrescriptions) Returns prescriptions for the requesting user")
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
        given(prescriptionsRepository.findByPatient(prescription.getPatient())).willReturn(List.of(prescription));
        // when - action or the behaviour that we are going test
        List<Prescription> prescriptions = prescriptionsService.getPrescriptions(prescription.getPatient());
        // then - verify the output
        assertThat(prescriptions.size()).isEqualTo(1);
        assertThat(prescriptions.get(0).getPatient().getId().equals(prescription.getPatient().getId()));
    }
    @DisplayName("(GetPatientPrescriptions) Returns prescriptions for the patient param when requested by USERROLE_PRACTITIONER")
    @Test
    public void givenExistingPrescription_whenGetPatientPrescriptions_thenReturnForPRACTITIONERRole() {
        //given - precondition or setup
        Prescription prescription = aPrescription()
                .withPractitioner(
                        aUserModel()
                                .withUsername("pat")
                                .withId(1L)
                                .withRole(USERROLE.PRACTITIONER))
                .withPatient(aUserModel()
                        .withId(2L)
                        .withRole(USERROLE.PATIENT))
                .build();
        UserModel patient = prescription.getPatient();
        UserModel practitioner = prescription.getPractitioner();
        given(userService.findUserModelById(patient.getId())).willReturn(Optional.of(patient));
        given(userService.findByLogin(practitioner.getUsername())).willReturn(practitioner);
        given(patientRegistrationRepository.findByUserModelAndPractitionerUserModel(patient,practitioner))
                .willReturn(List.of(new PatientRegistration()));
        given(prescriptionsRepository.findByPatient(patient)).willReturn(List.of(prescription));

        // when - action or the behaviour that we are going test
       List<PrescriptionOverviewData> prescriptions = prescriptionsService.getPatientPrescriptions(
                practitioner.getUsername(),
                patient.getId());
        // then - verify the output
        assertThat(prescriptions.size()).isEqualTo(1);
        assertThat(prescriptions.get(0).getPatientUsername().equals(patient.getUsername()));
    }

    @DisplayName("Return empty prescriptions when requested by USERROLE_USER")
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