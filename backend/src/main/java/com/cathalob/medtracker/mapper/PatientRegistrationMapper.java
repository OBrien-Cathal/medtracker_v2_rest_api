package com.cathalob.medtracker.mapper;


import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;

import java.util.List;

public class PatientRegistrationMapper {
    public List<PatientRegistrationData> patientRegistrationData(List<PatientRegistration>  patientRegistrations){
        return PatientRegistrationData(patientRegistrations);
    }

    public static List<PatientRegistrationData> PatientRegistrationData(List<PatientRegistration>  patientRegistrations){
        return patientRegistrations.stream().map((PatientRegistrationData::From
        )).toList();
    }

}
