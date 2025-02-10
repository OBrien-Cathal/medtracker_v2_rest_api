package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.testdata.BloodPressureReadingBuilder;
import com.cathalob.medtracker.testdata.DailyEvaluationBuilder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

        DailyEvaluation dailyEvaluation = DailyEvaluationBuilder.aDailyEvaluation().build();
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
    @Test
    public void givenDatesAndUserModelIds_whenFindByDailyEvaluationDatesAndIds_thenReturnReadings() {

        DailyEvaluation dailyEvaluation = DailyEvaluationBuilder.aDailyEvaluation().build();
        testEntityManager.persist(dailyEvaluation.getUserModel());
        testEntityManager.persist(dailyEvaluation);

        DailyEvaluation dailyEvaluation2 = DailyEvaluationBuilder.aDailyEvaluation().withRecordDate(LocalDate.now().plusDays(1)).build();
        testEntityManager.persist(dailyEvaluation2.getUserModel());
        testEntityManager.persist(dailyEvaluation2);



        BloodPressureReading reading1 = BloodPressureReadingBuilder.aBloodPressureReading().build();
        BloodPressureReading reading2 = BloodPressureReadingBuilder.aBloodPressureReading().build();
        reading1.setDailyEvaluation(dailyEvaluation);
        reading2.setDailyEvaluation(dailyEvaluation2);
        testEntityManager.persist(reading1);
        testEntityManager.persist(reading2);


//    when
        List<BloodPressureReading> foundReadings = bloodPressureReadingRepository.findByDailyEvaluationDatesAndIds(List.of(dailyEvaluation.getRecordDate(), dailyEvaluation2.getRecordDate()),
                List.of(dailyEvaluation.getUserModel().getId(), dailyEvaluation2.getUserModel().getId()));

//    then
        assertThat(foundReadings.size()).isEqualTo(2);
    }

    @Test
    public void givenDateAndUserModelId_whenFindByDailyEvaluationDatesAndIds_thenReturnReadings() {

        DailyEvaluation dailyEvaluation = DailyEvaluationBuilder.aDailyEvaluation().build();
        testEntityManager.persist(dailyEvaluation.getUserModel());
        testEntityManager.persist(dailyEvaluation);

        DailyEvaluation dailyEvaluation2 = DailyEvaluationBuilder.aDailyEvaluation().withRecordDate(LocalDate.now().plusDays(1)).build();
        testEntityManager.persist(dailyEvaluation2.getUserModel());
        testEntityManager.persist(dailyEvaluation2);



        BloodPressureReading reading1 = BloodPressureReadingBuilder.aBloodPressureReading().build();
        BloodPressureReading reading2 = BloodPressureReadingBuilder.aBloodPressureReading().build();
        reading1.setDailyEvaluation(dailyEvaluation);
        reading2.setDailyEvaluation(dailyEvaluation2);
        testEntityManager.persist(reading1);
        testEntityManager.persist(reading2);


//    when
        List<BloodPressureReading> foundReadings = bloodPressureReadingRepository.findByDailyEvaluationDatesAndIds(List.of(dailyEvaluation.getRecordDate()),
                List.of(dailyEvaluation.getUserModel().getId()));

//    then
        assertThat(foundReadings.size()).isEqualTo(1);
    }

}