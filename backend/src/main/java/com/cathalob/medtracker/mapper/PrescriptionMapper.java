package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.payload.data.PrescriptionDetailsData;
import com.cathalob.medtracker.payload.data.PrescriptionOverviewData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

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
        prescription.setId(prescriptionDetailsData.getId());
        prescription.setDoseMg(prescriptionDetailsData.getDoseMg());
        prescription.setBeginTime(prescriptionDetailsData.getBeginTime());
        if (prescriptionDetailsData.getBeginTime() == null) {
            prescription.setBeginTime(LocalDateTime.now());
        } else {
            prescription.setBeginTime(prescriptionDetailsData.getBeginTime());
        }
        if (prescriptionDetailsData.getEndTime() == null) {
            prescription.setEndTime(LocalDateTime.now().plusDays(5));
        } else {
            prescription.setEndTime(prescriptionDetailsData.getEndTime());
        }
        return prescription;
    }

    public static PrescriptionDetailsData PrescriptionDetails(Prescription prescription, List<PrescriptionScheduleEntry> byPrescription) {

        return PrescriptionDetailsData.builder()
                .doseMg(prescription.getDoseMg())
                .beginTime(prescription.getBeginTime())
                .endTime(prescription.getEndTime())
                .medication(prescription.getMedication())
                .patientId(prescription.getPatient().getId())
                .practitionerId(prescription.getPractitioner().getId())
                .id(prescription.getId())
                .prescriptionScheduleEntries(byPrescription)
                .build();
    }
}
