package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Stream;

import static com.cathalob.medtracker.testdata.UserModelBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
class PatientRegistrationRepositoryTests {
    @Autowired
    private PatientRegistrationRepository patientRegistrationRepository;
    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    public void givenPatientRegistration_whenSaved_thenReturnPatientRegistration() {
        //given - precondition or setup
        PatientRegistration patientRegistration = new PatientRegistration();
        UserModel practitioner = aPractitioner().build();
        UserModel registeringUser = aUserModel().build();

        patientRegistration.setPractitionerUserModel(practitioner);
        patientRegistration.setUserModel(registeringUser);

        testEntityManager.persist(registeringUser);
        testEntityManager.persist(practitioner);

        // when - action or the behaviour that we are going test
        PatientRegistration saved = patientRegistrationRepository.save(patientRegistration);
        // then - verify the output
        assertThat(saved.getId()).isGreaterThan(0L);
    }
    @DisplayName("Only return patient registrations for the given practitioner UserModel (findByPractitionerUserModel)")
    @Test
    public void givenSavedPatientRegistrationsForMultiplePractitioners_whenFindByPractitioner_thenReturnPatientRegistrationForOnlyOnePractitioner() {
        //given - precondition or setup

        UserModel practitioner = aPractitioner().build();
        UserModel practitioner2 = aNthPractitioner(2).build();

        testEntityManager.persist(practitioner);
        testEntityManager.persist(practitioner2);
        Stream.iterate(0, n -> n + 1).limit(3).forEach(n -> createAndPersistPatientRegistrationAndUserModelAndPersistPractitioner(practitioner, n));
        createAndPersistPatientRegistrationAndUserModelAndPersistPractitioner(practitioner2, 4);

        // when - action or the behaviour that we are going test
        List<PatientRegistration> byPractitionerUserModel = patientRegistrationRepository.findByPractitionerUserModel(practitioner);
        // then - verify the output
        assertThat(byPractitionerUserModel).size().isEqualTo(3);
        assertThat(byPractitionerUserModel).allMatch((e) ->
                e.getPractitionerUserModel().getId().equals(practitioner.getId())
                        && e.getPractitionerUserModel().getRole().equals(USERROLE.PRACTITIONER));
    }
@DisplayName("Return only patient registrations for the given UserModel (findByUserModel)")
    @Test
    public void givenSavedPatientRegistrationsForMultiplePatients_whenFindByPatient_thenReturnPatientRegistrationsForThatPatientOnly() {
        //given - precondition or setup

        UserModel practitioner = aPractitioner().build();
        UserModel practitioner2 = aNthPractitioner(2).build();
        UserModel requestingPatient = aPatient().build();
        UserModel otherPatient = aNthPatient(2).build();


        testEntityManager.persist(practitioner);
        testEntityManager.persist(practitioner2);
        testEntityManager.persist(requestingPatient);
        testEntityManager.persist(otherPatient);
        createAndPersistPatientRegistrationAndPersistPractitioner(practitioner, requestingPatient);
        createAndPersistPatientRegistrationAndPersistPractitioner(practitioner2, requestingPatient);
        createAndPersistPatientRegistrationAndPersistPractitioner(practitioner2, otherPatient);

        // when - action or the behaviour that we are going test
        List<PatientRegistration> byPractitionerUserModel = patientRegistrationRepository.findByUserModel(requestingPatient);
        // then - verify the output
        assertThat(byPractitionerUserModel).size().isEqualTo(2);
        assertThat(byPractitionerUserModel).allMatch((e) ->
                e.getUserModel().getId().equals(requestingPatient.getId())
                        && !e.isRegistered());
    }

    @DisplayName("Return one patient registration for combination of practitioner and patient user")
    @Test
    public void givenSavedPatientRegistrations_whenFindByUserModelAndPractitionerUserModel_thenReturnValuesForThatCombination() {
        //given - precondition or setup

        UserModel practitioner = aPractitioner().build();
        UserModel practitioner2 = aNthPractitioner(2).build();
        UserModel requestingPatient = aPatient().build();
        UserModel otherPatient = aNthPatient(2).build();

        testEntityManager.persist(practitioner);
        testEntityManager.persist(practitioner2);
        testEntityManager.persist(requestingPatient);
        testEntityManager.persist(otherPatient);
        createAndPersistPatientRegistrationAndPersistPractitioner(practitioner, requestingPatient);
        createAndPersistPatientRegistrationAndPersistPractitioner(practitioner2, requestingPatient);
        createAndPersistPatientRegistrationAndPersistPractitioner(practitioner2, otherPatient);

        // when - action or the behaviour that we are going test
        List<PatientRegistration> byPractitionerUserModel = patientRegistrationRepository.findByUserModelAndPractitionerUserModel(requestingPatient,practitioner);
        // then - verify the output
        assertThat(byPractitionerUserModel).size().isEqualTo(1);
        assertThat(byPractitionerUserModel).allMatch((e) ->
                e.getUserModel().getId().equals(requestingPatient.getId())
                        && e.getPractitionerUserModel().getId().equals(practitioner.getId()));
    }

    private void createAndPersistPatientRegistrationAndUserModelAndPersistPractitioner(UserModel practitioner, int index) {
        UserModel registeringUser = aNthUser(index).build();
        testEntityManager.persist(registeringUser);
        createAndPersistPatientRegistrationAndPersistPractitioner(practitioner, registeringUser);
    }

    private void createAndPersistPatientRegistrationAndPersistPractitioner(UserModel practitioner, UserModel registeringUser) {
        PatientRegistration patientRegistration = new PatientRegistration();
        patientRegistration.setPractitionerUserModel(practitioner);
        patientRegistration.setUserModel(registeringUser);

        testEntityManager.persist(practitioner);
        testEntityManager.persist(patientRegistration);


    }

}