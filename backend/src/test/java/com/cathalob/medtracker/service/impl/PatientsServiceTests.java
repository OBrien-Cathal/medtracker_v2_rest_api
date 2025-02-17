package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.exception.validation.ApprovePatientRegistrationValidatorException;
import com.cathalob.medtracker.exception.validation.PatientRegistrationException;
import com.cathalob.medtracker.factory.PatientRegistrationFactory;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.repository.RoleChangeRepository;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.validate.service.PatientServiceValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static com.cathalob.medtracker.testdata.UserModelBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class PatientsServiceTests {

    @Mock
    private PatientRegistrationRepository patientRegistrationRepository;
    @Mock
    private UserService userService;
    @Mock
    private PatientRegistrationFactory factory;
    @Mock
    private PatientServiceValidator validator;
    @Mock
    private RoleChangeRepository roleChangeRepository;
    @InjectMocks
    private PatientsService patientsService;


    @DisplayName("GetPatientRegistrations returns non empty list for requests made by PRACTITIONER")
    @Test
    public void givenExistingPatientRegistrations_whenGetPatientRegistrationsAsPRACTITIONER_thenReturnPatientRegistrations() {
        //given - precondition or setup
        UserModel userModel = aPractitioner().build();

        List<PatientRegistration> patientRegistrations = List.of(
                new PatientRegistration(),
                new PatientRegistration());

        given(userService.findByLogin(userModel.getUsername())).willReturn(userModel);
        given(patientRegistrationRepository.findByPractitionerUserModel(userModel)).willReturn(patientRegistrations);

        // when - action or the behaviour that we are going test
        List<PatientRegistration> registrations = patientsService.getPatientRegistrations(userModel.getUsername());
        // then - verify the output
        verify(patientRegistrationRepository, never()).findByUserModel(userModel);
        assertThat(registrations).size().isEqualTo(2);
    }

    @DisplayName("GetPatientRegistrations returns non empty list for requests made by USER")
    @Test
    public void givenExistingPatientRegistrations_whenGetPatientRegistrationsAsUSER_thenReturnPatientRegistrations() {
        //given - precondition or setup
        UserModel userModel = aUserModel().build();

        List<PatientRegistration> patientRegistrations = List.of(
                new PatientRegistration(),
                new PatientRegistration());
        given(userService.findByLogin(userModel.getUsername())).willReturn(userModel);

        given(patientRegistrationRepository.findByUserModel(userModel)).willReturn(patientRegistrations);
        // when - action or the behaviour that we are going test
        List<PatientRegistration> registrations = patientsService.getPatientRegistrations(userModel.getUsername());
        // then - verify the output
        verify(patientRegistrationRepository, never()).findByPractitionerUserModel(userModel);
        assertThat(registrations).size().isEqualTo(2);
    }

    @DisplayName("GetPatientRegistrations returns empty list for requests made by ADMIN")
    @Test
    public void givenExistingPatientRegistrations_whenGetPatientRegistrationsAsADMIN_thenReturnEmptyList() {
        //given - precondition or setup
        UserModel userModel = anAdmin().build();

        given(userService.findByLogin(userModel.getUsername())).willReturn(userModel);

        // when - action or the behaviour that we are going test
        List<PatientRegistration> registrations = patientsService.getPatientRegistrations(userModel.getUsername());
        // then - verify the output
        verify(patientRegistrationRepository, never()).findByPractitionerUserModel(userModel);
        verify(patientRegistrationRepository, never()).findByUserModel(userModel);
        assertThat(registrations).isEmpty();
    }

    @DisplayName("Successful patient registration returns success response")
    @Test
    public void givenPatientRegistrationRequest_whenRegisterPatient_thenReturnPatientRegistrationResponse() {
        //given - precondition or setup

        UserModel toRegister = aUserModel().build();
        UserModel practitioner = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();
        given(userService.findByLogin(toRegister.getUsername())).willReturn(toRegister);
        given(userService.findUserModelById(1L)).willReturn(Optional.of(practitioner));

        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                toRegister,
                practitioner,
                false);
        given(factory.patientRegistration(toRegister, practitioner))
                .willReturn(patientRegistration);


        given(patientRegistrationRepository.findByUserModelAndPractitionerUserModel(toRegister, practitioner))
                .willReturn(List.of());
        given(patientRegistrationRepository.save(patientRegistration))
                .willReturn(patientRegistration);

        // when - action or the behaviour that we are going test
        Long patientRegistrationId = patientsService.registerPatient(toRegister.getUsername(), practitioner.getId());

        // then - verify the output

        assertThat(patientRegistrationId).isNotNull();
        assertThat(patientRegistrationId).isEqualTo(1L);

    }

    @DisplayName("Save will not occur after validation failure exception thrown")
    @Test
    public void givenFailedValidationPatientRegistrationRequest_whenRegisterPatient_thenThrowValidationException() throws PatientRegistrationException {
        //given - precondition or setup

        UserModel toRegister = aUserModel().build();
        UserModel practitioner = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();
        given(userService.findByLogin(toRegister.getUsername())).willReturn(toRegister);
        given(userService.findUserModelById(1L)).willReturn(Optional.of(practitioner));

        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                toRegister,
                practitioner,
                false);
        given(factory.patientRegistration(toRegister, practitioner))
                .willReturn(patientRegistration);

        given(patientRegistrationRepository.findByUserModelAndPractitionerUserModel(toRegister, practitioner))
                .willReturn(List.of(patientRegistration));
        doThrow(new PatientRegistrationException(List.of("Test")))
                .when(validator).validateRegisterPatient(patientRegistration, patientRegistration);
//        given(patientRegistrationRepository.save(patientRegistration))
//                .willReturn(patientRegistration);

        // when - action or the behaviour that we are going test
        Assertions.assertThrows(PatientRegistrationException.class,
                () -> patientsService.registerPatient(toRegister.getUsername(), practitioner.getId()));

        // then - verify the output
        verify(patientRegistrationRepository, never()).save(any(PatientRegistration.class));
    }


    @DisplayName("(RegisterPatient) fail due to non existent practitioner user")
    @Test
    public void givenPatientRegistrationRequestWithBogusPractitionerId_whenRegisterPatient_thenReturnFailedPatientRegistrationResponse() {
        //given - precondition or setup
        UserModel toRegister = aUserModel().build();
        UserModel practitioner = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();
        given(userService.findByLogin(toRegister.getUsername())).willReturn(toRegister);
        given(userService.findUserModelById(practitioner.getId())).willReturn(Optional.empty());
        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                toRegister,
                null,
                false);

        given(factory.patientRegistration(toRegister, null))
                .willReturn(patientRegistration);
        doThrow(new PatientRegistrationException(List.of("Test")))
                .when(validator).validateRegisterPatient(patientRegistration, null);
        // when - action or the behaviour that we are going test
        assertThrows(PatientRegistrationException.class, () -> patientsService.registerPatient(toRegister.getUsername(), practitioner.getId()));

        // then - verify the output
        verify(patientRegistrationRepository, never()).save(any(PatientRegistration.class));
    }


    @DisplayName("Successful approvePatientRegistration - returns reg id")
    @Test
    public void givenApprovePatientRegistrationRequest_whenApprovedPatientRegistration_thenReturnFailedApprovePatientRegistrationResponse() {
        //given - precondition or setup
        UserModel toRegister = aPatient().build();
        UserModel practitioner = aPractitioner().build();

        given(userService.findByLogin(practitioner.getUsername()))
                .willReturn(practitioner);

        PatientRegistration patientRegistration = new PatientRegistration(1L, toRegister, practitioner, false);
        given(patientRegistrationRepository.findById(patientRegistration.getId()))
                .willReturn(Optional.of(patientRegistration));

        given(patientRegistrationRepository.save(patientRegistration))
                .willReturn(patientRegistration);
        // when - action or the behaviour that we are going test
        Long savedId = patientsService.approvePatientRegistration(practitioner.getUsername(), patientRegistration.getId());

        // then - verify the output
        assertThat(savedId).isNotNull();
        assertThat(savedId).isEqualTo(1L);
    }

    @DisplayName("Fail validation: (approvePatientRegistration) - does not update and throws exception")
    @Test
    public void givenApprovePatientRegistrationRequestFailingValidation_whenApprovedPatientRegistration_thenThrowException() {
        //given - precondition or setup
        UserModel toRegister = aUserModel().build();
        UserModel notPractitioner = anAdmin().build();
        given(userService.findByLogin(notPractitioner.getUsername()))
                .willReturn(notPractitioner);

        PatientRegistration patientRegistration = new PatientRegistration(1L, toRegister, notPractitioner, false);
        given(patientRegistrationRepository.findById(patientRegistration.getId()))
                .willReturn(Optional.of(patientRegistration));

        doThrow(new ApprovePatientRegistrationValidatorException(List.of("Test")))
                .when(validator).validateApprovePatientRegistration(patientRegistration, notPractitioner);

        // when - action or the behaviour that we are going test
        assertThrows(ApprovePatientRegistrationValidatorException.class,
                () -> patientsService.approvePatientRegistration(notPractitioner.getUsername(), patientRegistration.getId()));


        // then - verify the output
        verify(patientRegistrationRepository, never()).save(any(PatientRegistration.class));
        verify(roleChangeRepository, never()).save(any(RoleChange.class));
    }
}