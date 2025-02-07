package com.cathalob.medtracker.testdata.service;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.payload.request.graph.GraphDataForDateRangeRequest;
import com.cathalob.medtracker.testdata.MedicationBuilder;
import com.cathalob.medtracker.testdata.UserModelBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.cathalob.medtracker.testdata.DailyEvaluationBuilder.aDailyEvaluation;
import static com.cathalob.medtracker.testdata.PrescriptionBuilder.aPrescription;
import static com.cathalob.medtracker.testdata.PrescriptionScheduleEntryBuilder.aPrescriptionScheduleEntry;

public class DoseServiceTestDataBuilder {

    public void data() {


        //given - precondition or setup
        LocalDate requestDate = LocalDate.now();
        GraphDataForDateRangeRequest request = GraphDataForDateRangeRequest.builder().start(requestDate).end(requestDate).build();

        UserModelBuilder patientBuilder = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();

        MedicationBuilder med1 = MedicationBuilder.aMedication().withId(1L).withName("Med1");
        MedicationBuilder med2 = MedicationBuilder.aMedication().withId(2L).withName("Med2");

        Prescription prescription1 = aPrescription().withId(1L)
                .with(med1)
                .withBeginTime(LocalDateTime.of(requestDate, LocalTime.now()))
                .build();
        Prescription prescription2 = aPrescription().withId(2L)
                .with(med2)
                .withBeginTime(LocalDateTime.of(requestDate, LocalTime.now()))
                .build();

        prescription1.setPatient(patient);
        prescription2.setPatient(patient);

        PrescriptionScheduleEntry entryP1e1 = aPrescriptionScheduleEntry().withDayStage(DAYSTAGE.WAKEUP).withId(1L).build();
        entryP1e1.setPrescription(prescription1);
        PrescriptionScheduleEntry entryP1e2 = aPrescriptionScheduleEntry().withDayStage(DAYSTAGE.BEDTIME).withId(2L).build();
        entryP1e2.setPrescription(prescription1);

        PrescriptionScheduleEntry entryP2E3 = aPrescriptionScheduleEntry().withDayStage(DAYSTAGE.WAKEUP).withId(3L).build();
        entryP2E3.setPrescription(prescription2);
        PrescriptionScheduleEntry entryP2e4 = aPrescriptionScheduleEntry().withDayStage(DAYSTAGE.BEDTIME).withId(4L).build();
        entryP2e4.setPrescription(prescription2);

        List<PrescriptionScheduleEntry> pse = new ArrayList<>(List.of(
                entryP1e1,
                entryP1e2,
                entryP2E3,
                entryP2e4));


        DailyEvaluation evaluation = aDailyEvaluation().withRecordDate(requestDate).with(patientBuilder).build();


    }

    public GraphDataForDateRangeRequest graphDataRequestYesterdayToTomorrow() {

        LocalDate requestDate = LocalDate.now();
        GraphDataForDateRangeRequest request = GraphDataForDateRangeRequest.builder()
                .start(requestDate.plusDays(-1))
                .end(requestDate.plusDays(1))
                .build();
        return request;
    }

    public PrescriptionScheduleEntry pSEForPrescription(Prescription prescription, DAYSTAGE daystage) {
        PrescriptionScheduleEntry entry = aPrescriptionScheduleEntry().withDayStage(daystage).withId(1L).build();
        entry.setPrescription(prescription);
        return entry;
    }

    public void addPSEs(UserModel patient,
                                                   Medication medication, Integer doseMg,
                                                   List<DAYSTAGE> dayStages,
                                                   LocalDate begin, LocalDate end,
                                                   List<PrescriptionScheduleEntry> pseList) {

        long pID = (long) pseList.stream().map(PrescriptionScheduleEntry::getPrescription).distinct().toList().size() + 1;

        Prescription prescription = aPrescription()
                .withId(pID)
                .withBeginTime(LocalDateTime.of(begin, LocalTime.now()))
                .withEndTime(LocalDateTime.of(end, LocalTime.now()))
                .withDoseMg(doseMg)
                .build();
        prescription.setPatient(patient);
        prescription.setMedication(medication);
        for (DAYSTAGE ds : dayStages) {
            PrescriptionScheduleEntry entry = aPrescriptionScheduleEntry()
                    .withDayStage(ds)
                    .withId((long) pseList.size() + 1)
                    .build();
            entry.setPrescription(prescription);
            pseList.add(entry);
        }
    }
}
