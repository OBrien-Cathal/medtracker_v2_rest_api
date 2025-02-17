package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.payload.data.PrescriptionDetailsData;
import com.cathalob.medtracker.payload.data.PrescriptionOverviewData;
import com.cathalob.medtracker.payload.response.GetPrescriptionDetailsResponse;
import com.cathalob.medtracker.payload.response.SubmitPrescriptionDetailsResponse;
import com.cathalob.medtracker.puremodel.PrescriptionDetails;

import java.time.LocalDateTime;
import java.util.List;

public class PrescriptionMapper {

    public Prescription prescription(PrescriptionDetailsData prescriptionDetailsData) {
        return Prescription(prescriptionDetailsData);
    }

    public SubmitPrescriptionDetailsResponse submitPrescriptionResponse(Long prescriptionId) {
        return SubmitPrescriptionDetailsResponse.Success(prescriptionId);
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

    public GetPrescriptionDetailsResponse getPrescriptionDetailsResponse(PrescriptionDetails prescriptionDetails) {
        return GetPrescriptionDetailsResponse(prescriptionDetails);
    }

    public static GetPrescriptionDetailsResponse GetPrescriptionDetailsResponse(PrescriptionDetails prescriptionDetails) {
        return GetPrescriptionDetailsResponse.Success(PrescriptionDetails(prescriptionDetails));
    }

    public PrescriptionDetailsData prescriptionDetails(PrescriptionDetails prescriptionDetails) {
        return PrescriptionDetails(prescriptionDetails);
    }

    public static PrescriptionDetailsData PrescriptionDetails(PrescriptionDetails prescriptionDetails) {
        Prescription prescription = prescriptionDetails.getPrescription();
        List<PrescriptionScheduleEntry> prescriptionScheduleEntries = prescriptionDetails.getPrescriptionScheduleEntries();

        return PrescriptionDetailsData.builder()
                .doseMg(prescription.getDoseMg())
                .beginTime(prescription.getBeginTime())
                .endTime(prescription.getEndTime())
                .medication(prescription.getMedication())
                .patientId(prescription.getPatient().getId())
                .practitionerId(prescription.getPractitioner().getId())
                .id(prescription.getId())
                .prescriptionScheduleEntries(prescriptionScheduleEntries)
                .build();
    }

    public List<PrescriptionOverviewData> overviews(List<Prescription> prescriptions) {
        return Overviews(prescriptions);

    }

    public static List<PrescriptionOverviewData> Overviews(List<Prescription> prescriptions) {
        return prescriptions.stream().map(PrescriptionMapper::Overview).toList();

    }

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


}
