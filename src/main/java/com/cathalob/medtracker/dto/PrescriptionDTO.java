package com.cathalob.medtracker.dto;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.prescription.Medication;
import lombok.Data;

@Data
public class PrescriptionDTO {
    private Medication medication;
    private int doseMg;
    private DAYSTAGE daystage;
}
