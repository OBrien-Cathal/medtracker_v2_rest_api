package com.cathalob.medtracker.validate.model.patient;

import com.cathalob.medtracker.exception.validation.ApprovePatientRegistrationValidatorException;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.cathalob.medtracker.testdata.UserModelBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApprovePatientRegistrationValidatorTests {

    @DisplayName("Valid registration approval raises no errors")
    @Test
    public void givenSuccessfulApproval_whenValidate_thenThrowNoException() {
        //given - precondition or setup
        UserModel toRegister = aUserModel().build();
        UserModel practitioner = aUserModel().withId(1L).withRole(USERROLE.PRACTITIONER).build();

        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                toRegister,
                practitioner,
                false);

        ApprovePatientRegistrationValidator validator = new ApprovePatientRegistrationValidator(patientRegistration, practitioner);
        // when - action or the behaviour that we are going test
        assertDoesNotThrow(validator::validate);

        // then - verify the output
        assertThat(validator.getErrors()).isEmpty();
    }


    @DisplayName("Approving user must be practitioner")
    @Test
    public void givenApprovingUserNotPRACTITIONER_whenValidate_thenThrowError() {
        //given - precondition or setup
        UserModel toRegister = aUserModel().build();
        UserModel practitioner = anAdmin().build();

        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                toRegister,
                practitioner,
                false);

        ApprovePatientRegistrationValidator validator = new ApprovePatientRegistrationValidator(patientRegistration, practitioner);
        // when - action or the behaviour that we are going test
        assertThrows(ApprovePatientRegistrationValidatorException.class, validator::validate);

        // then - verify the output

        assertThat(validator.getErrors().size()).isEqualTo(1);
        assertThat(validator.getErrors().get(0)).isEqualTo("User has role 'ADMIN', where only 'PRACTITIONER' are allowed.");
    }

    @DisplayName("Registration to approve must exist")
    @Test
    public void givenRegistrationDoesNotExist_whenValidate_thenThrowError() {
        //given - precondition or setup
        UserModel practitioner = anAdmin().build();

        ApprovePatientRegistrationValidator validator = new ApprovePatientRegistrationValidator(null, practitioner);
        // when - action or the behaviour that we are going test
        assertThrows(ApprovePatientRegistrationValidatorException.class, validator::validate);

        // then - verify the output
        assertThat(validator.getErrors().size()).isEqualTo(1);
        assertThat(validator.getErrors().get(0)).isEqualTo("Missing Validation Subject: ApprovePatientRegistrationValidator");
        System.out.println(validator.getErrors());
    }


    @DisplayName("Approval of other practitioner registrations is forbidden")
    @Test
    public void givenRegistrationForOtherPractitioner_whenValidate_thenThrowError() {
        //given - precondition or setup
        UserModel toRegister = aUserModel().build();
        UserModel practitioner = aPractitioner().build();

        UserModel otherPractitioner = aNthPractitioner(2).withId(1L).build();

        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                toRegister,
                otherPractitioner,
                false);


        ApprovePatientRegistrationValidator validator = new ApprovePatientRegistrationValidator(patientRegistration, practitioner);
        // when - action or the behaviour that we are going test
        assertThrows(ApprovePatientRegistrationValidatorException.class, validator::validate);

        // then - verify the output
        assertThat(validator.getErrors().size()).isEqualTo(1);
        assertThat(validator.getErrors().get(0)).isEqualTo("Only possible to approve own registrations");
        System.out.println(validator.getErrors());
    }

    @DisplayName("Can only approve registrations for USER and PATIENT user roles")
    @Test
    public void givenRegisteringUsersMustBePATIENT_or_USER_whenValidate_thenThrowError() {
        //given - precondition or setup
        UserModel toRegister = anAdmin().build();
        UserModel practitioner = aPractitioner().withId(1L).build();

        PatientRegistration patientRegistration = new PatientRegistration(
                1L,
                toRegister,
                practitioner,
                false);


        ApprovePatientRegistrationValidator validator = new ApprovePatientRegistrationValidator(patientRegistration, practitioner);
        // when - action or the behaviour that we are going test
        assertThrows(ApprovePatientRegistrationValidatorException.class, validator::validate);

        // then - verify the output
        assertThat(validator.getErrors().size()).isEqualTo(1);
        assertThat(validator.getErrors().get(0)).isEqualTo("User has role 'ADMIN', where only 'PATIENT, USER' are allowed.");
        System.out.println(validator.getErrors());
    }

}
