package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.AccountRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AccountRegistrationRepositoryTests {
    @Autowired
    AccountRegistrationRepository accountRegistrationRepository;
    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    public void givenAccountRegistration_whenSaved_thenReturnAccountRegistrationWithId() {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aUserModel().build();
        AccountRegistration accountRegistration = new AccountRegistration();
        accountRegistration.setUserModel(userModel);
        accountRegistration.setRegistrationTime(LocalDateTime.now());
        accountRegistration.setConfirmed(false);
        accountRegistration.setRegistrationId(UUID.randomUUID());

        // when - action or the behaviour that we are going test
        AccountRegistration saved = accountRegistrationRepository.save(accountRegistration);
        // then - verify the output

        Assertions.assertThat(saved.getId()).isGreaterThan(0);
    }

    @DisplayName("Unconfirmed registrations returned from query for unconfirmed registrations")
    @Test
    public void givenExistingUnconfirmedAccountRegistration_whenFindByUserModelIdAndConfirmedAndRegistrationId_thenReturnAccountRegistrationWithId() {
        //given - precondition or setup
        AccountRegistration accountRegistration = createAndPersistUnconfirmedAccountRegistration();
        // when - action or the behaviour that we are going test
        List<AccountRegistration> found = accountRegistrationRepository.findByUserModelIdAndConfirmedAndRegistrationId(accountRegistration.getUserModel().getId(),
                false,
                accountRegistration.getRegistrationId());
        // then - verify the output
        Assertions.assertThat(found).isNotEmpty();
    }

    @DisplayName("Confirmed registrations NOT returned from query for unconfirmed registrations")
    @Test
    public void givenExistingConfirmedAccountRegistration_whenFindByUserModelIdAndConfirmedAndRegistrationId_thenReturnEmptyList() {
        //given - precondition or setup
        AccountRegistration accountRegistration = createAndPersistConfirmedAccountRegistration();
        // when - action or the behaviour that we are going test
        List<AccountRegistration> found = accountRegistrationRepository.findByUserModelIdAndConfirmedAndRegistrationId(accountRegistration.getUserModel().getId(),
                false,
                accountRegistration.getRegistrationId());
        // then - verify the output
        Assertions.assertThat(found).isEmpty();
    }

    @DisplayName("Confirmed registrations returned from query for confirmed registrations")
    @Test
    public void givenExistingConfirmedAccountRegistration_whenFindByUserModelIdAndConfirmed_thenReturnFoundRegistration() {
        //given - precondition or setup
        AccountRegistration accountRegistration = createAndPersistConfirmedAccountRegistration();
        // when - action or the behaviour that we are going test
        List<AccountRegistration> found = accountRegistrationRepository.findByUserModelIdAndConfirmed(accountRegistration.getUserModel().getId(),
                true);
        // then - verify the output
        Assertions.assertThat(found).isNotEmpty();
    }




    private AccountRegistration createAndPersistConfirmedAccountRegistration() {
        return createAndPersistAccountRegistration(true);
    }

    private AccountRegistration createAndPersistUnconfirmedAccountRegistration() {
        return createAndPersistAccountRegistration(false);
    }

    private AccountRegistration createAndPersistAccountRegistration(boolean isConfirmed) {
        UserModel userModel = UserModelBuilder.aUserModel().build();
        AccountRegistration accountRegistration = new AccountRegistration();
        accountRegistration.setUserModel(userModel);
        LocalDateTime now = LocalDateTime.now();
        accountRegistration.setRegistrationTime(now.plusDays(-1));
        accountRegistration.setRegistrationId(UUID.randomUUID());

        accountRegistration.setConfirmed(isConfirmed);
        if (isConfirmed){
            accountRegistration.setConfirmationTime(now);
        }
        testEntityManager.persist(accountRegistration);
        return accountRegistration;
    }

}