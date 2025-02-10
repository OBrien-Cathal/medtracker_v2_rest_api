package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.mapper.DailyEvaluationMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.repository.DailyEvaluationRepository;
import com.cathalob.medtracker.testdata.DailyEvaluationBuilder;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EvaluationDataServiceTests {
    @InjectMocks
    private EvaluationDataService evaluationDataService;
    @Mock
    private DailyEvaluationRepository dailyEvaluationRepository;
    @Mock
    private DailyEvaluationMapper dailyEvaluationMapper;

    @Test
    public void givenRangeTodayToTodayPlus3_whenFindActiveForPatient_thenReturnEvalForFullRange() {
        //given - precondition or setup
        LocalDate date = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(3);

        UserModelBuilder patientBuilder = aUserModel().withId(1L).withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();
        ArrayList<DailyEvaluation> dailyEvaluations = new ArrayList<>();
        addDailyEvaluationsBetween(patient, date, endDate, dailyEvaluations);

        given(dailyEvaluationRepository.findDailyEvaluationsByUserModel(patient)).willReturn(dailyEvaluations);
        // when - action or the behaviour that we are going test
        List<DailyEvaluation> activeEvaluations = evaluationDataService.findDailyEvaluationsByUserModelActiveBetween(patient, date, endDate);
        // then - verify the output
        assertThat(activeEvaluations.size()).isEqualTo(4);
        assertThat(activeEvaluations.size()).isEqualTo(dailyEvaluations.size());
    }

    @Test
    public void givenDateWithNoExistingDailyEvaluation_whenFindOrCreateDailyEvaluationForPatientAndDate_thenReturnNewlyCreatedDailyEvaluation() {
        //given - precondition or setup
        LocalDate date = LocalDate.now();


        UserModelBuilder patientBuilder = aUserModel().withId(1L).withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();
        ArrayList<DailyEvaluation> dailyEvaluations = new ArrayList<>();
        addDailyEvaluationsBetween(patient, date, date, dailyEvaluations);

        DailyEvaluation dailyEvaluation = dailyEvaluations.get(0);

        given(dailyEvaluationRepository.findById(dailyEvaluation.getDailyEvaluationIdClass()))
                .willReturn(Optional.empty());
        given(dailyEvaluationRepository.save(dailyEvaluation))
                .willReturn(dailyEvaluation);
        given(dailyEvaluationMapper.toDailyEvaluation(date, patient))
                .willReturn(dailyEvaluation);
        // when - action or the behaviour that we are going test
        DailyEvaluation newEvaluation = evaluationDataService.findOrCreateDailyEvaluationForPatientAndDate(patient, date);
        // then - verify the output

        assertThat(newEvaluation).isNotNull();
        assertThat(newEvaluation.getRecordDate()).isEqualTo(date);
    }


    private void addDailyEvaluationsBetween(UserModel patient,
                                         LocalDate start,
                                         LocalDate end,
                                         List<DailyEvaluation> list) {
        LocalDate current = start;
        while (current.isEqual(end) || current.isBefore(end)) {
            DailyEvaluation newDE = DailyEvaluationBuilder.aDailyEvaluation()

                    .withRecordDate(current).build();
            newDE.setUserModel(patient);

            list.add(newDE);
            current = current.plusDays(1);
        }

    }
}