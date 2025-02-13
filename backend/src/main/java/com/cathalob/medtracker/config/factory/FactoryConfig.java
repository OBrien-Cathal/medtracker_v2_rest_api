package com.cathalob.medtracker.config.factory;

import com.cathalob.medtracker.factory.AccountDetailsFactory;
import com.cathalob.medtracker.factory.AccountRegistrationFactory;
import com.cathalob.medtracker.factory.DoseServiceModelFactory;
import com.cathalob.medtracker.factory.AuthenticationFactory;
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
}
