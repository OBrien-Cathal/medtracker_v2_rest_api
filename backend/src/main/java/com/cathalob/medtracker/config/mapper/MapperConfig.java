package com.cathalob.medtracker.config.mapper;

import com.cathalob.medtracker.mapper.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public DailyEvaluationMapper dailyEvaluationMapper() {
        return new DailyEvaluationMapper();
    }


    @Bean
    public DoseMapper doseMapper() {
        return new DoseMapper();
    }

    @Bean
    public BloodPressureMapper bloodPressureMapper() {
        return new BloodPressureMapper();
    }

    @Bean
    public PrescriptionMapper prescriptionMapper() {
        return new PrescriptionMapper();
    }

    @Bean
    public PatientRegistrationMapper patientRegistrationMapper() {
        return new PatientRegistrationMapper();
    }


    @Bean
    public RoleChangeMapper roleChangeMapper() {
        return new RoleChangeMapper();
    }

    @Bean
    public BulkDataMapper bulkDataMapper() {
        return new BulkDataMapper();
    }

    @Bean
    public SignInRecordsMapper signInRecordsMapper() {
        return new SignInRecordsMapper();
    }
}

