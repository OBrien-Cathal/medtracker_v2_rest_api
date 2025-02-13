package com.cathalob.medtracker.repository;


import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.prescription.Prescription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.cathalob.medtracker.testdata.PrescriptionBuilder.aNthPrescription;
import static com.cathalob.medtracker.testdata.PrescriptionBuilder.aPrescription;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PrescriptionsRepositoryTests {
    @Autowired
    private PrescriptionsRepository prescriptionsRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void givenPrescription_whenSaved_thenReturnSavedPrescription() {

//        given
        Prescription prescription = aPrescription().build();
        testEntityManager.persist(prescription.getPatient());
        testEntityManager.persist(prescription.getPractitioner());
        testEntityManager.persist(prescription.getMedication());

//        when
        Prescription savedPrescription = prescriptionsRepository.save(prescription);

//        then
        assertThat(savedPrescription.getId()).isGreaterThan(0);
    }

    @Test
    public void givenSavedPrescriptions_whenFindByPatient_thenReturnSavedPrescriptionsForThePatient() {

//        given
        Prescription prescription = aPrescription().build();
        persistPrescriptionAndDependencies(prescription);
        UserModel patientToFindBy = prescription.getPatient();

        persistPrescriptionAndDependencies(aNthPrescription(2).build());
        persistPrescriptionAndDependencies(aNthPrescription(3).build());
        persistPrescriptionAndDependencies(aNthPrescription(4).build());

        Prescription otherPrescription = aNthPrescription(5).build();
        otherPrescription.setPatient(patientToFindBy);
        persistPrescriptionAndDependencies(otherPrescription);

//        when
        System.out.println(patientToFindBy.getId());
        List<Prescription> byPatient = prescriptionsRepository.findByPatient(patientToFindBy);

//        then
        assertThat(byPatient.size()).isEqualTo(2);
    }

    private void persistPrescriptionDependencies(Prescription prescription) {
        testEntityManager.persist(prescription.getPatient());
        testEntityManager.persist(prescription.getPractitioner());
        testEntityManager.persist(prescription.getMedication());

    }
    private void persistPrescriptionAndDependencies(Prescription prescription) {
        persistPrescriptionDependencies(prescription);
        testEntityManager.persist(prescription);

    }
}