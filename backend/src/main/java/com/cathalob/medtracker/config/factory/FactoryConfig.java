package com.cathalob.medtracker.config.factory;

import com.cathalob.medtracker.factory.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FactoryConfig {
    @Bean
    public DoseServiceModelFactory doseServiceModelFactory() {
        return new DoseServiceModelFactory();
    }

    @Bean
    public AccountRegistrationFactory accountRegistrationFactory() {
        return new AccountRegistrationFactory();
    }

    @Bean
    public AccountDetailsFactory accountDetailsFactory() {
        return new AccountDetailsFactory();
    }

    @Bean
    public AuthenticationFactory userModelFactory(){
        return new AuthenticationFactory();
    }

    @Bean
    public PatientRegistrationFactory patientRegistrationFactory(){
        return new PatientRegistrationFactory();
    }

    @Bean
    public RoleChangeServiceFactory roleChangeServiceFactory() {return new RoleChangeServiceFactory();}
}
