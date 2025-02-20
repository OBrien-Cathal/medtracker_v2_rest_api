package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.factory.SignInRecordsServiceFactory;
import com.cathalob.medtracker.model.SignInRecord;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.repository.SignInRecordsRepository;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class SignInRecordsServiceImplTests {
    @InjectMocks
    private SignInRecordsServiceImpl signInRecordsService;

    @Mock
    private SignInRecordsServiceFactory factory;

    @Mock
    private SignInRecordsRepository signInRecordsRepository;

    @DisplayName("Existing sign in records are returned when requested")
    @Test
    public void givenSignInRecords_whenGetSignInRecords_thenReturnExistingSignInRecords() {
        //given - precondition or setup
        UserModel userModel1 = UserModelBuilder.aUserModel().withId(1L).build();
        UserModel userModel2 = UserModelBuilder.aUserModel().withId(2L).build();

        List<SignInRecord> records = List.of(
                SignInRecord.builder().signInTime(LocalDateTime.now()).userModel(userModel1).build(),
                SignInRecord.builder().signInTime(LocalDateTime.now().plusDays(-1)).userModel(userModel2).build());

        given(signInRecordsRepository.findAll())
                .willReturn(records);

        // when - action or the behaviour that we are going test

        List<SignInRecord> signInRecords = signInRecordsService.getSignInRecords();

        // then - verify the output
        Assertions.assertThat(signInRecords.size()).isEqualTo(2);
    }

    @DisplayName("Sign in record created and saved")
    @Test
    public void givenUserModelSignIn_whenAddSignInRecord_thenVerifyRecordIsSaved() {
        //given - precondition or setup
        UserModel userModel1 = UserModelBuilder.aUserModel().withId(1L).build();

        SignInRecord record = SignInRecord.builder().signInTime(LocalDateTime.now()).userModel(userModel1).build();

        given(factory.signInRecord(userModel1))
                .willReturn(record);
        given(signInRecordsRepository.save(record))
                .willReturn(record);

        // when - action or the behaviour that we are going test
        signInRecordsService.addSignInRecord(userModel1);


        // then - verify the output
        verify(signInRecordsRepository, times(1)).save(record);

    }

}