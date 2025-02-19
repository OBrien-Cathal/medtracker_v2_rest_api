package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.testdata.BloodPressureReadingBuilder;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.cathalob.medtracker.testdata.BloodPressureReadingBuilder.aBloodPressureReading;
import static com.cathalob.medtracker.testdata.RoleChangeBuilder.aRoleChange;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class BulkDataServiceTests {
    @Mock
    private PrescriptionsService prescriptionsService;
    @Mock
    private BloodPressureDataService bloodPressureDataService;
    @Mock
    private DoseService doseService;
    @Mock
    private EvaluationDataService evaluationDataService;
    @Mock
    private UserService userService;

    @InjectMocks
    private BulkDataService bulkDataService;


    @DisplayName("GetAllBloodPressureReadings returns collection sorted for excel file")
    @Test
    public void givenSuccessfulSubmitRoleChange_whenSubmitRoleChange_thenReturnSavedRoleChange() {
        //given - precondition or setup
        UserModel userModel = UserModelBuilder.aPatient().build();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime startPlus_1Hour = start.plusHours(1);
        LocalDateTime startMinus_1Hour = start.plusHours(-1);

        LocalDateTime startPlus_1Day = start.plusDays(1);
        LocalDateTime startPlus_1Day1Hour = start.plusDays(1).plusHours(1);
        LocalDateTime startPlus_1Day2Hours = start.plusDays(1).plusHours(2);

        LocalDateTime startPlus_2Day = start.plusDays(2);
        LocalDateTime startPlus_2Day1Hour = start.plusDays(2).plusHours(1);
        LocalDateTime startPlus_2Day2Hours = start.plusDays(2).plusHours(2);

        LocalDateTime startMinus_2Day = start.plusDays(-2);
        LocalDateTime startMinus_2Day1Hour = start.plusDays(-2).plusHours(-1);
        LocalDateTime startMinus_2Day2Hours = start.plusDays(-2).plusHours(-2);


        LocalDateTime startMinus_1Day = start.plusDays(-1);
        LocalDateTime startMinus_1Day1Hour = start.plusDays(-1).plusHours(-1);
        LocalDateTime startMinus_1Day2Hours = start.plusDays(-1).plusHours(-2);


        BloodPressureReading bp1 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(start)
                .withDaystage(DAYSTAGE.WAKEUP)
                .build();
        BloodPressureReading bp2 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startPlus_1Hour)
                .withDaystage(DAYSTAGE.BEDTIME)
                .build();
        BloodPressureReading bp3 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startMinus_1Hour)
                .withDaystage(DAYSTAGE.MIDDAY)
                .build();

        BloodPressureReading bp4 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startPlus_1Day)
                .withDaystage(DAYSTAGE.WAKEUP)
                .build();
        BloodPressureReading bp5 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startPlus_1Day1Hour)
                .withDaystage(DAYSTAGE.BEDTIME)
                .build();
        BloodPressureReading bp6 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startPlus_1Day2Hours)
                .withDaystage(DAYSTAGE.MIDDAY)
                .build();


        BloodPressureReading bp7 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startPlus_2Day)
                .withDaystage(DAYSTAGE.WAKEUP)
                .build();
        BloodPressureReading bp8 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startPlus_2Day1Hour)
                .withDaystage(DAYSTAGE.BEDTIME)
                .build();
        BloodPressureReading bp9 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startPlus_2Day2Hours)
                .withDaystage(DAYSTAGE.MIDDAY)
                .build();

        BloodPressureReading bp10 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startMinus_2Day)
                .withDaystage(DAYSTAGE.WAKEUP)
                .build();
        BloodPressureReading bp11 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startMinus_2Day1Hour)
                .withDaystage(DAYSTAGE.BEDTIME)
                .build();
        BloodPressureReading bp12 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startMinus_2Day2Hours)
                .withDaystage(DAYSTAGE.MIDDAY)
                .build();

        BloodPressureReading bp13 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startMinus_1Day)
                .withDaystage(DAYSTAGE.WAKEUP)
                .build();
        BloodPressureReading bp14 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startMinus_1Day1Hour)
                .withDaystage(DAYSTAGE.BEDTIME)
                .build();
        BloodPressureReading bp15 = aBloodPressureReading()
                .withReadingTimeAndEvaluationDate(startMinus_1Day2Hours)
                .withDaystage(DAYSTAGE.MIDDAY)
                .build();


        List<BloodPressureReading> list = List.of(bp1, bp2, bp3, bp4, bp5, bp6, bp7, bp8, bp9, bp10, bp11, bp12, bp13, bp14, bp15);


        given(bloodPressureDataService.getAllBloodPressureReadings(userModel.getUsername()))
                .willReturn(list);
        // when
        List<BloodPressureReading> bloodPressureFileContents = bulkDataService.getAllBloodPressureReadings(userModel.getUsername());

        // then - verify the output
        for (BloodPressureReading r : bloodPressureFileContents) {
            System.out.println(r.getReadingTime().toLocalDate() + " -- " + r.getReadingTime().toLocalTime() + " -- " + r.getDayStage());
        }

        List<DAYSTAGE> dayStages = bloodPressureFileContents.stream().map(reading -> reading.getDayStage()).toList();
        assertThat(
                dayStages.equals(
                        List.of(DAYSTAGE.WAKEUP, DAYSTAGE.MIDDAY, DAYSTAGE.BEDTIME,
                                DAYSTAGE.WAKEUP, DAYSTAGE.MIDDAY, DAYSTAGE.BEDTIME,
                                DAYSTAGE.WAKEUP, DAYSTAGE.MIDDAY, DAYSTAGE.BEDTIME,
                                DAYSTAGE.WAKEUP, DAYSTAGE.MIDDAY, DAYSTAGE.BEDTIME,
                                DAYSTAGE.WAKEUP, DAYSTAGE.MIDDAY, DAYSTAGE.BEDTIME

                        ))).isTrue();
    }
}