package com.cathalob.medtracker.config.validator;

import com.cathalob.medtracker.validate.service.PatientServiceValidator;
import com.cathalob.medtracker.validate.service.RoleChangeServiceValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfig {

    @Bean
    public PatientServiceValidator patientServiceValidator() {
        return new PatientServiceValidator();

    }

    @Bean
    public RoleChangeServiceValidator roleChangeServiceValidator() {
        return new RoleChangeServiceValidator();

    }


}
