package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.exception.validation.PrescriptionValidatorException;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.repository.MedicationRepository;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.repository.PrescriptionScheduleEntryRepository;
import com.cathalob.medtracker.repository.PrescriptionsRepository;

import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.testdata.MedicationBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.cathalob.medtracker.testdata.PrescriptionBuilder.aPrescription;
import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


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
    @Mock
    private MedicationRepository medicationRepository;
    @Mock
    private PrescriptionScheduleEntryRepository prescriptionScheduleEntryRepository;

    @DisplayName("(GetPrescriptions) Returns prescriptions for existing requesting user")
    @Test
    public void givenExistingPrescription_whenGetPrescriptions_thenReturnForPatientRole() {
        //given - precondition or setup
        Prescription prescription = existingPrescription();
        given(prescriptionsRepository.findByPatient(prescription.getPatient())).willReturn(List.of(prescription));
        // when - action or the behaviour that we are going test
        List<Prescription> prescriptions = prescriptionsService.getPrescriptions(prescription.getPatient());
        // then - verify the output
        assertThat(prescriptions.size()).isEqualTo(1);
        assertThat(prescriptions.get(0).getPatient().getId().equals(prescription.getPatient().getId()));
    }

    private static Prescription existingPrescription() {
        return aPrescription().withId(1L)
                .withPractitioner(
                        aUserModel()
                                .withId(1L)
                                .withRole(USERROLE.PRACTITIONER))
                .withPatient(aUserModel()
                        .withId(2L)
                        .withRole(USERROLE.PATIENT))
                .build();
    }

    @DisplayName("(GetPatientPrescriptions) Returns prescriptions for requested patient if registered")
    @Test
    public void givenExistingPrescription_whenGetPatientPrescriptions_thenReturnForPRACTITIONERRole() {
        //given - precondition or setup
        Prescription prescription = existingPrescription();

        UserModel patient = prescription.getPatient();
        UserModel practitioner = prescription.getPractitioner();

        given(userService.findUserModelById(patient.getId())).willReturn(Optional.of(patient));
        given(userService.findByLogin(practitioner.getUsername())).willReturn(practitioner);
        given(patientRegistrationRepository.findByUserModelAndPractitionerUserModel(patient, practitioner))
                .willReturn(List.of(new PatientRegistration()));
        given(prescriptionsRepository.findByPatient(patient)).willReturn(List.of(prescription));

        // when - action or the behaviour that we are going test
        List<Prescription> prescriptions = prescriptionsService.getPatientPrescriptions(
                practitioner.getUsername(),
                patient.getId());
        // then - verify the output
        assertThat(prescriptions.size()).isEqualTo(1);
        assertThat(prescriptions.get(0).getPatient().getUsername().equals(patient.getUsername()));
    }

    @DisplayName("(GetPatientPrescriptions) Returns EMPTY prescriptions for requested patient if NOT registered")
    @Test
    public void givenExistingPrescriptionAndUnregisteredPatient_whenGetPatientPrescriptions_thenReturnEmptyList() {
        //given - precondition or setup
        Prescription prescription = existingPrescription();

        UserModel patient = prescription.getPatient();
        UserModel practitioner = prescription.getPractitioner();
        given(userService.findUserModelById(patient.getId())).willReturn(Optional.of(patient));
        given(userService.findByLogin(practitioner.getUsername())).willReturn(practitioner);
        given(patientRegistrationRepository.findByUserModelAndPractitionerUserModel(patient, practitioner))
                .willReturn(List.of());

        // when - action or the behaviour that we are going test
        List<Prescription> prescriptions = prescriptionsService.getPatientPrescriptions(
                practitioner.getUsername(),
                patient.getId());

        // then - verify the output
        assertThat(prescriptions).isEmpty();

    }

    @DisplayName("SubmitPrescription update NON existent prescription throws validation error")
    @Test
    public void givenNonExistentPrescriptionId_whenSubmitPrescriptions_thenThrowValidationError() {
        //given - precondition or setup
        Prescription prescription = existingPrescription();


        UserModel patient = prescription.getPatient();
        UserModel practitioner = prescription.getPractitioner();
        given(prescriptionsRepository.findById(prescription.getId())).willReturn(Optional.empty());

        // when - action or the behaviour that we are going test
        assertThrows(PrescriptionValidatorException.class, () -> prescriptionsService.submitPrescription(
                practitioner.getUsername(),
                prescription,
                List.of(),
                patient.getId(),
                prescription.getMedication().getId()));

        // then - verify the output
        verify(prescriptionsRepository, never()).save(any(Prescription.class));
    }

    @Disabled("Validation is too restrictive, should be replaced with a check if any doses have been submitted yet")
    @DisplayName("Validator for SubmitPrescription when begin is in past throws validation error")
    @Test
    public void givenValidatorFailure_whenSubmitPrescriptions_thenThrowValidationError() {
        //given - precondition or setup
        Prescription prescription = existingPrescription();
        prescription.setMedication(MedicationBuilder.aMedication().withId(1L).build());
        prescription.setBeginTime(LocalDateTime.now().plusDays(-2));

        UserModel patient = prescription.getPatient();
        UserModel practitioner = prescription.getPractitioner();
        PatientRegistration patientRegistration = new PatientRegistration();

        patientRegistration.setPractitionerUserModel(practitioner);


        given(prescriptionsRepository.findById(prescription.getId())).willReturn(Optional.of(prescription));
        given(userService.findUserModelById(patient.getId())).willReturn(Optional.of(patient));
        given(userService.findByLogin(practitioner.getUsername())).willReturn(practitioner);
        given(patientRegistrationRepository.findByUserModelAndPractitionerUserModel(patient, practitioner))
                .willReturn(List.of(patientRegistration));

        given(medicationRepository.findById(prescription.getMedication().getId()))
                .willReturn(Optional.of(prescription.getMedication()));

        // when - action or the behaviour that we are going test
        assertThrows(PrescriptionValidatorException.class, () -> prescriptionsService.submitPrescription(
                practitioner.getUsername(),
                prescription,
                List.of(),
                patient.getId(),
                prescription.getMedication().getId()));

        // then - verify the output
        verify(prescriptionsRepository, never()).save(any(Prescription.class));
    }

    @DisplayName("SubmitPrescription (Update) for valid prescription returns saved id")
    @Test
    public void givenSuccessfulSaveOfExistingPrescription_whenSubmitPrescriptions_thenReturnID() {
        //given - precondition or setup
        Prescription prescription = existingPrescription();


        prescription.setMedication(MedicationBuilder.aMedication().withId(1L).build());

        UserModel patient = prescription.getPatient();
        UserModel practitioner = prescription.getPractitioner();
        PatientRegistration patientRegistration = new PatientRegistration();

        patientRegistration.setPractitionerUserModel(practitioner);

        given(prescriptionsRepository.findById(prescription.getId())).willReturn(Optional.of(prescription));
        given(userService.findUserModelById(patient.getId())).willReturn(Optional.of(patient));
        given(userService.findByLogin(practitioner.getUsername())).willReturn(practitioner);
        given(patientRegistrationRepository.findByUserModelAndPractitionerUserModel(patient, practitioner))
                .willReturn(List.of(patientRegistration));

        given(medicationRepository.findById(prescription.getMedication().getId()))
                .willReturn(Optional.of(prescription.getMedication()));

        given(prescriptionScheduleEntryRepository.findByPrescription(prescription))
                .willReturn(List.of());
        given(prescriptionScheduleEntryRepository.saveAll(List.of()))
                .willReturn(List.of());

        given(prescriptionsRepository.save(prescription))
                .willReturn(prescription);

        // when - action or the behaviour that we are going test
        Long savedId = prescriptionsService.submitPrescription(
                practitioner.getUsername(),
                prescription,
                List.of(),
                patient.getId(),
                prescription.getMedication().getId());

        // then - verify the output
        assertThat(savedId).isEqualTo(prescription.getId());
    }

    @DisplayName("SubmitPrescription (Add) for valid prescription returns saved id")
    @Test
    public void givenSuccessfulSaveOfNewPrescription_whenSubmitPrescriptions_thenReturnID() {
        //given - precondition or setup
        Prescription prescription = existingPrescription();
        prescription.setId(null);

        prescription.setMedication(MedicationBuilder.aMedication().withId(1L).build());

        UserModel patient = prescription.getPatient();
        UserModel practitioner = prescription.getPractitioner();
        PatientRegistration patientRegistration = new PatientRegistration();

        patientRegistration.setPractitionerUserModel(practitioner);

        given(userService.findUserModelById(patient.getId())).willReturn(Optional.of(patient));
        given(userService.findByLogin(practitioner.getUsername())).willReturn(practitioner);
        given(patientRegistrationRepository.findByUserModelAndPractitionerUserModel(patient, practitioner))
                .willReturn(List.of(patientRegistration));

        given(medicationRepository.findById(prescription.getMedication().getId()))
                .willReturn(Optional.of(prescription.getMedication()));

        given(prescriptionScheduleEntryRepository.findByPrescription(prescription))
                .willReturn(List.of());
        given(prescriptionScheduleEntryRepository.saveAll(List.of()))
                .willReturn(List.of());

        given(prescriptionsRepository.save(prescription))
                .willReturn(prescription);

        // when - action or the behaviour that we are going test
        Long savedId = prescriptionsService.submitPrescription(
                practitioner.getUsername(),
                prescription,
                List.of(),
                patient.getId(),
                prescription.getMedication().getId());

        // then - verify the output
        assertThat(savedId).isEqualTo(prescription.getId());
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