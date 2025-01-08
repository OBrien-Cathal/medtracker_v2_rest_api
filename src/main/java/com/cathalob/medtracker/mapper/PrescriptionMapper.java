package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.payload.data.PrescriptionData;

public class PrescriptionMapper {
    public static PrescriptionData Overview(Prescription prescription){
        return new PrescriptionData(
                prescription.getId(),
                prescription.getDoseMg(),
                prescription.getMedication(),
                prescription.getPatient().getUsername(),
                prescription.getPractitioner().getUsername(),
                prescription.getBeginTime(),
                prescription.getEndTime());
    }
}
