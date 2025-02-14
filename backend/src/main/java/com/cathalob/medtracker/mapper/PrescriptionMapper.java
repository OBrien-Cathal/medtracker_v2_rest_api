package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.payload.data.PrescriptionDetailsData;
import com.cathalob.medtracker.payload.data.PrescriptionOverviewData;
import com.cathalob.medtracker.puremodel.PrescriptionDetails;

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


    public Prescription prescription(PrescriptionDetailsData prescriptionDetailsData) {
        return Prescription(prescriptionDetailsData);
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
        prescription.setEndTime(prescriptionDetailsData.getEndTime());

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

    public PrescriptionDetailsData prescriptionDetails(PrescriptionDetails prescriptionDetails) {

        return PrescriptionDetails(prescriptionDetails.getPrescription(), prescriptionDetails.getPrescriptionScheduleEntries());
    }

    public List<PrescriptionOverviewData> overviews(List<Prescription> prescriptions) {
        return Overviews(prescriptions);

    }

    public static List<PrescriptionOverviewData> Overviews(List<Prescription> prescriptions) {
        return prescriptions.stream().map(PrescriptionMapper::Overview).toList();

    }



}
