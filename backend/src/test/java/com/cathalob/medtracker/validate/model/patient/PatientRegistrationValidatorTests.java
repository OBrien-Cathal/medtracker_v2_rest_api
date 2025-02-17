package com.cathalob.medtracker.validate.model.patient;

import com.cathalob.medtracker.exception.validation.PatientRegistrationException;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.cathalob.medtracker.testdata.UserModelBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

class PatientRegistrationValidatorTests {
    @DisplayName("Valid registration raises no errors")
    @Test
    public void givenValidRegistration_whenValidate_thenDoNotThrowException() {
        //given - precondition or setup
        UserModel toRegister = aUserModel().build();
        UserModel practitioner = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();

        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                toRegister,
                practitioner,
                false);

        PatientRegistrationValidator validator = new PatientRegistrationValidator(patientRegistration, null);
        // when - action or the behaviour that we are going test
        assertDoesNotThrow(validator::validate);

        // then - verify the output
        Assertions.assertThat(validator.getErrors()).isEmpty();
    }

    @DisplayName("Registration with no practitioner raises error")
    @Test
    public void givenRegistrationWithNoPractitioner_whenValidate_thenThrowException() {
        //given - precondition or setup
        UserModel toRegister = aUserModel().build();

        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                toRegister,
                null,
                false);

        PatientRegistrationValidator validator = new PatientRegistrationValidator(patientRegistration, null);
        // when - action or the behaviour that we are going test
        assertThrows(PatientRegistrationException.class, validator::validate);

        // then - verify the output
        Assertions.assertThat(validator.getErrors().size()).isEqualTo(1);
    }

    @DisplayName("Duplicate PatientRegistration raises error")
    @Test
    public void givenDuplicateRegistration_whenValidate_thenThrowException() {
        //given - precondition or setup
        UserModel toRegister = aUserModel().build();
        UserModel practitioner = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();

        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                toRegister,
                practitioner,
                false);

        PatientRegistrationValidator validator = new PatientRegistrationValidator(patientRegistration, patientRegistration);
        // when - action or the behaviour that we are going test
        // then - verify the output
        assertThrows(PatientRegistrationException.class, validator::validate);
        Assertions.assertThat(validator.getErrors().size()).isEqualTo(1);
        System.out.println(validator.getErrors());
    }

    @DisplayName("PatientRegistration for not (PATIENT, USER) UserRole raises error")
    @Test
    public void givenPatientRegistrationWithNon_PATIENT_USER_whenValidate_thenThrowException() {
        //given - precondition or setup
        UserModel toRegister = aPractitioner().build();
        UserModel practitioner = aNthPractitioner(2).withId(1L).build();

        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                toRegister,
                practitioner,
                false);

        PatientRegistrationValidator validator = new PatientRegistrationValidator(patientRegistration, null);
        // when - action or the behaviour that we are going test
        // then - verify the output
        assertThrows(PatientRegistrationException.class, validator::validate);
        Assertions.assertThat(validator.getErrors().size()).isEqualTo(1);
        System.out.println(validator.getErrors());
    }

}