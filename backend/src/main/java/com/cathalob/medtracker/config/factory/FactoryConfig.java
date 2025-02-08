package com.cathalob.medtracker.config.factory;

import com.cathalob.medtracker.factory.DoseServiceModelFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FactoryConfig {
    @Bean
    public DoseServiceModelFactory doseServiceModelFactory(){
        return new DoseServiceModelFactory();
    }

}
