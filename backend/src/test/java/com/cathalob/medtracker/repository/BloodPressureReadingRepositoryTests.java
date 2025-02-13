package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.testdata.BloodPressureReadingBuilder;
import com.cathalob.medtracker.testdata.DailyEvaluationBuilder;

import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.cathalob.medtracker.testdata.DailyEvaluationBuilder.aDailyEvaluation;
import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BloodPressureReadingRepositoryTests {
    @Autowired
    private BloodPressureReadingRepository bloodPressureReadingRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test

    public void givenBloodPressureReading_whenSaved_thenReturnSavedBloodPressureReading() {

        DailyEvaluation dailyEvaluation = aDailyEvaluation().build();
        testEntityManager.persist(dailyEvaluation.getUserModel());
        testEntityManager.persist(dailyEvaluation);

        BloodPressureReading bloodPressureReading = new BloodPressureReading();
        bloodPressureReading.setReadingTime(LocalDateTime.now());
        bloodPressureReading.setDiastole(80);
        bloodPressureReading.setSystole(120);
        bloodPressureReading.setHeartRate(60);
        bloodPressureReading.setDayStage(DAYSTAGE.MIDDAY);
        bloodPressureReading.setDailyEvaluation(dailyEvaluation);

//    when
        BloodPressureReading saved = bloodPressureReadingRepository.save(bloodPressureReading);

//    then
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @DisplayName("Query returns readings for multiple dates queried and not other dates")
    @Test
    public void givenReadingPerDateForUser_whenFindByDailyEvaluationDatesAndUserModelId_thenReturn2Readings() {

        DailyEvaluation dailyEvaluation = aDailyEvaluation().build();
        testEntityManager.persist(dailyEvaluation.getUserModel());
        testEntityManager.persist(dailyEvaluation);

        DailyEvaluation dailyEvaluation2 = aDailyEvaluation()
                .with(aUserModel()
                        .withUsername("user2@user.com"))
                .withRecordDate(LocalDate.now().plusDays(1))
                .build();
        dailyEvaluation2.setUserModel(dailyEvaluation.getUserModel());
        testEntityManager.persist(dailyEvaluation2);

        DailyEvaluation dailyEvaluation3 = aDailyEvaluation()
                .with(aUserModel()
                        .withUsername("user3@user.com"))
                .withRecordDate(LocalDate.now().plusDays(2))
                .build();

        dailyEvaluation3.setUserModel(dailyEvaluation.getUserModel());
        testEntityManager.persist(dailyEvaluation3);


        BloodPressureReading reading1 = BloodPressureReadingBuilder.aBloodPressureReading().build();
        BloodPressureReading reading2 = BloodPressureReadingBuilder.aBloodPressureReading().build();
        BloodPressureReading reading3 = BloodPressureReadingBuilder.aBloodPressureReading().build();

        reading1.setDailyEvaluation(dailyEvaluation);
        reading2.setDailyEvaluation(dailyEvaluation2);
        reading3.setDailyEvaluation(dailyEvaluation3);

        testEntityManager.persist(reading1);
        testEntityManager.persist(reading2);
        testEntityManager.persist(reading3);


//    when
        List<BloodPressureReading> foundReadings = bloodPressureReadingRepository.findByDailyEvaluationDatesAndUserModelId(
                List.of(dailyEvaluation.getRecordDate(), dailyEvaluation2.getRecordDate()),
                dailyEvaluation.getUserModel().getId());

//    then
        assertThat(foundReadings.size()).isEqualTo(2);
    }

    @DisplayName("Query does not return readings belonging to other users for a date range")
    @Test
    public void given1ReadingForUserModel_whenFindByDailyEvaluationDatesAndUserModelId_thenReturn1Reading() {

        DailyEvaluation dailyEvaluation = aDailyEvaluation().build();
        testEntityManager.persist(dailyEvaluation.getUserModel());
        testEntityManager.persist(dailyEvaluation);

        DailyEvaluation dailyEvaluation2 = aDailyEvaluation()
                .with(aUserModel()
                        .withUsername("user2@user.com"))
                .withRecordDate(LocalDate.now().plusDays(1))
                .build();
        testEntityManager.persist(dailyEvaluation2.getUserModel());
        testEntityManager.persist(dailyEvaluation2);


        BloodPressureReading reading1 = BloodPressureReadingBuilder.aBloodPressureReading().build();
        BloodPressureReading reading2 = BloodPressureReadingBuilder.aBloodPressureReading().build();
        reading1.setDailyEvaluation(dailyEvaluation);
        reading2.setDailyEvaluation(dailyEvaluation2);
        testEntityManager.persist(reading1);
        testEntityManager.persist(reading2);


//    when
        List<BloodPressureReading> foundReadings = bloodPressureReadingRepository.findByDailyEvaluationDatesAndUserModelId(
                List.of(dailyEvaluation.getRecordDate(), dailyEvaluation2.getRecordDate()),
                dailyEvaluation.getUserModel().getId());

//    then
        assertThat(foundReadings.size()).isEqualTo(1);
    }

}