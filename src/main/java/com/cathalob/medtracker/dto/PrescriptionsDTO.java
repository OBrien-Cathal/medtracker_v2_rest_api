package com.cathalob.medtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PrescriptionsDTO {
    private List<PrescriptionDTO> prescriptions;


}
