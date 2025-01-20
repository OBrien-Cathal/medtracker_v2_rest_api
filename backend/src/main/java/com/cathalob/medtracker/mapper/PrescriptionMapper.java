package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.payload.data.PrescriptionDetailsData;
import com.cathalob.medtracker.payload.data.PrescriptionOverviewData;

public class PrescriptionMapper {
    public static PrescriptionOverviewData Overview(Prescription prescription) {
        return new PrescriptionOverviewData(
                prescription.getId(),
                prescription.getDoseMg(),
                prescription.getMedication(),
                prescription.getPatient().getUsername(),
                prescription.getPractitioner().getUsername(),
                prescription.getBeginTime(),
                prescription.getEndTime());
    }

    public static Prescription Prescription(PrescriptionDetailsData prescriptionDetailsData) {
        Prescription prescription = new Prescription();
        prescription.setDoseMg(prescriptionDetailsData.getDoseMg());
        prescription.setBeginTime(prescriptionDetailsData.getBeginTime());
        prescription.setEndTime(prescriptionDetailsData.getEndTime());
        return prescription;
    }
}
