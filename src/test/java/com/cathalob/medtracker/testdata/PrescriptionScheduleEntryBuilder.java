package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;


public class PrescriptionScheduleEntryBuilder {
    private Long id;

    private PrescriptionBuilder prescriptionBuilder = PrescriptionBuilder.aPrescription();

    private DAYSTAGE dayStage = DAYSTAGE.WAKEUP;

    public PrescriptionScheduleEntryBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public PrescriptionScheduleEntryBuilder with(PrescriptionBuilder prescriptionBuilder) {
        this.prescriptionBuilder = prescriptionBuilder;
        return this;
    }

    public PrescriptionScheduleEntryBuilder withDayStage(DAYSTAGE dayStage) {
        this.dayStage = dayStage;
        return this;
    }

    public static PrescriptionScheduleEntryBuilder aPrescriptionScheduleEntry(){
        return new PrescriptionScheduleEntryBuilder();
    }

    public PrescriptionScheduleEntryBuilder() {
    }

    public PrescriptionScheduleEntryBuilder(PrescriptionScheduleEntryBuilder copy) {
        this.id = copy.id;
        this.prescriptionBuilder = copy.prescriptionBuilder;
        this.dayStage = copy.dayStage;
    }
    public PrescriptionScheduleEntryBuilder but(){
        return new PrescriptionScheduleEntryBuilder(this);
    }
    public PrescriptionScheduleEntry build(){
        return new PrescriptionScheduleEntry(id,prescriptionBuilder.build(),dayStage);
    }
}
