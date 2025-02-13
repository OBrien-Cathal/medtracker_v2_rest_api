package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.Dose;


import java.time.LocalDateTime;
import java.util.List;

import static com.cathalob.medtracker.testdata.UserModelBuilder.*;

public class DoseBuilder {
    private Long id;
    private DailyEvaluationBuilder dailyEvaluationBuilder = new DailyEvaluationBuilder();

    private LocalDateTime doseTime = LocalDateTime.now();

    private PrescriptionScheduleEntryBuilder prescriptionScheduleEntryBuilder = new PrescriptionScheduleEntryBuilder();

    private UserModelBuilder patientBuilder = aPatient();
    private UserModelBuilder practitionerBuilder = aPractitioner();

    private boolean taken = false;

    public DoseBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public DoseBuilder withDailyEvaluationBuilder(DailyEvaluationBuilder dailyEvaluationBuilder) {
        this.dailyEvaluationBuilder = dailyEvaluationBuilder;
        return this;
    }

    public DoseBuilder withPatient(UserModelBuilder userModelBuilder) {
        this.patientBuilder = userModelBuilder;
        return this;
    }

    public DoseBuilder withPractitioner(UserModelBuilder userModelBuilder) {
        this.practitionerBuilder = userModelBuilder;
        return this;
    }

    public DoseBuilder withDoseTime(LocalDateTime doseTime) {
        this.doseTime = doseTime;
        return this;
    }

    public DoseBuilder withPrescriptionScheduleEntryBuilder(PrescriptionScheduleEntryBuilder prescriptionScheduleEntryBuilder) {
        this.prescriptionScheduleEntryBuilder = prescriptionScheduleEntryBuilder;
        return this;
    }

    public DoseBuilder withTaken(boolean taken) {
        this.taken = taken;
        return this;
    }

    public DoseBuilder() {
    }

    public DoseBuilder(DoseBuilder copy) {
        this.id = copy.id;
        this.dailyEvaluationBuilder = copy.dailyEvaluationBuilder;
        this.doseTime = copy.doseTime;
        this.prescriptionScheduleEntryBuilder = copy.prescriptionScheduleEntryBuilder;
        this.taken = copy.taken;
    }

    public static DoseBuilder aDose() {
        return new DoseBuilder();
    }

    public static DoseBuilder aSecondDose() {
        return aNthDose(2);
    }

    public static DoseBuilder aThirdDose() {
        return aNthDose(3);
    }

    public static DoseBuilder aNthDose(int ordinal) {
        DoseBuilder doseBuilder = new DoseBuilder();

        doseBuilder.withPatient(aNthPatient(ordinal));
        doseBuilder.withPractitioner(aNthPractitioner(ordinal));

        return doseBuilder;
    }


    public DoseBuilder but() {
        return new DoseBuilder(this);
    }

    public Dose build() {
        PrescriptionScheduleEntry prescriptionScheduleEntry = prescriptionScheduleEntryBuilder.build();
        DailyEvaluation dailyEvaluation = dailyEvaluationBuilder.build();
        UserModel patient = patientBuilder.build();
        UserModel practitioner = practitionerBuilder.build();

        dailyEvaluation.setUserModel(patient);
        prescriptionScheduleEntry.getPrescription().setPatient(patient);
        prescriptionScheduleEntry.getPrescription().setPractitioner(practitioner);

        return new Dose(id, dailyEvaluation, doseTime, prescriptionScheduleEntry, taken);
    }


    public static List<Dose> dosesFor(List<PrescriptionScheduleEntry> pse, DailyEvaluation dailyEvaluation) {
        return pse.stream().map(prescriptionScheduleEntry -> {
            Dose dose = aDose()
                    .withId(prescriptionScheduleEntry.getId())
                    .withDoseTime(LocalDateTime.now()).build();
            dose.setPrescriptionScheduleEntry(prescriptionScheduleEntry);
            dose.setEvaluation(dailyEvaluation);
            return dose;

        }).toList();
    }
}
