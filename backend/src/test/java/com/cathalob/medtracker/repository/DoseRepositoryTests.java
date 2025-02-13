package com.cathalob.medtracker.repository;


import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.Dose;

import static com.cathalob.medtracker.testdata.DoseBuilder.aDose;
import static com.cathalob.medtracker.testdata.DoseBuilder.aSecondDose;
import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;

import com.cathalob.medtracker.testdata.DailyEvaluationBuilder;
import com.cathalob.medtracker.testdata.PrescriptionBuilder;
import com.cathalob.medtracker.testdata.UserModelBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;


@DataJpaTest
@ActiveProfiles("test")
class DoseRepositoryTests {
    @Autowired
    private DoseRepository doseRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void givenDose_whenSaved_thenReturnSavedDose() {
//          given
        Dose dose = aDose().build();
        persistPrerequisites(dose);

//      when
        Dose saved = doseRepository.save(dose);

//      then
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    public void givenDose_whenSavedAndQueried_thenReturnOnlyDosesForUserModelId() {
//          given
        Dose dose = aDose().build();
        Dose otherDose = aSecondDose()
                .build();

        persistPrerequisites(dose);
        persistPrerequisites(otherDose);
        testEntityManager.persist(otherDose);

//      when
        doseRepository.save(dose);
        List<Dose> dosesForUserId = doseRepository.findDosesForUserId(dose.getPrescriptionScheduleEntry().getPrescription().getPatient().getId());

//      then
        assertThat(doseRepository.findAll().size()).isEqualTo(2);
        assertThat(dosesForUserId.size()).isEqualTo(1);
        assertThat(dosesForUserId).allMatch(retrievedDose -> retrievedDose.getEvaluation().getUserModel().getId() > (0));

    }


    @Test
    public void givenDose_whenFindByDailyEvaluation_thenReturnDoseList() {
//          given
        Dose dose = aDose().build();
        Dose otherDose = aSecondDose()
                .build();

        persistPrerequisites(dose);
        persistPrerequisites(otherDose);
        testEntityManager.persist(otherDose);
        testEntityManager.persist(dose);

//      when
        List<Dose> byEvaluation = doseRepository.findByEvaluation(
                dose.getEvaluation());

//      then
        assertThat(doseRepository.findAll().size()).isEqualTo(2);
        assertThat(byEvaluation.size()).isEqualTo(1);
        assertThat(byEvaluation).allMatch(retrievedDose -> retrievedDose.getEvaluation().getUserModel().getId() > (0));

    }

    @Test
    public void given1DosePerDateForUser_whenFindByDailyEvaluationDatesAndUserModelId_thenReturn2Doses() {
//          given
        Dose dose = aDose().build();
        Dose otherDose = aSecondDose().withDailyEvaluationBuilder(
                        DailyEvaluationBuilder.aDailyEvaluation()
                                .withRecordDate(dose.getEvaluation().getRecordDate().plusDays(1)))
                .build();


        UserModel userModelWithMultipleDoses = dose.getEvaluation().getUserModel();

        otherDose.getEvaluation().setUserModel(userModelWithMultipleDoses);
        otherDose.setPrescriptionScheduleEntry(dose.getPrescriptionScheduleEntry());


        persistPrerequisites(dose);
        testEntityManager.persist(dose);
        persistPrerequisites(otherDose);
        testEntityManager.persist(otherDose);

//      when
        List<Dose> byEvaluation = doseRepository.findByDailyEvaluationDatesAndUserModelId(
                List.of(
                        dose.getEvaluation().getRecordDate(), otherDose.getEvaluation().getRecordDate()),
                userModelWithMultipleDoses.getId()
        );

//      then
        assertThat(byEvaluation.size()).isEqualTo(2);
    }

    @Test
    public void given1DoseForEachUser_whenFindByDailyEvaluationDatesAndUserModelId_thenReturnSingleDose() {
//          given
        Dose dose = aDose().build();
        Dose otherDose = aSecondDose()
                .build();

        persistPrerequisites(dose);
        persistPrerequisites(otherDose);
        testEntityManager.persist(otherDose);
        testEntityManager.persist(dose);

//      when
        List<Dose> byEvaluation = doseRepository.findByDailyEvaluationDatesAndUserModelId(
                List.of(
                        dose.getEvaluation().getRecordDate()),
                dose.getEvaluation().getUserModel().getId()
        );

//      then
        assertThat(byEvaluation.size()).isEqualTo(1);
    }


    private void persistPrerequisites(Dose dose) {
        PrescriptionScheduleEntry prescriptionScheduleEntry = dose.getPrescriptionScheduleEntry();
        Prescription prescription = prescriptionScheduleEntry.getPrescription();


        testEntityManager.persist(dose.getEvaluation().getUserModel());
        testEntityManager.persist(dose.getEvaluation());
        testEntityManager.persist(prescription.getPractitioner());
        testEntityManager.persist(prescription.getMedication());
        testEntityManager.persist(prescription);
        testEntityManager.persist(prescriptionScheduleEntry);
    }
}