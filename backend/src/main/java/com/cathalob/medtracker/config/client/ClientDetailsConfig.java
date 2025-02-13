package com.cathalob.medtracker.config.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientDetailsConfig {

    @Value("${website.localOrigin}")
    private String baseWebsiteURL;


    @Bean
    public ClientDetails clientDetails() {
        return new ClientDetails(baseWebsiteURL);
    }

}
