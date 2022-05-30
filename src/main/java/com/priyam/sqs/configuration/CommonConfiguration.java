package com.priyam.sqs.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

@Order(1)
@Configuration
@RequiredArgsConstructor
public class CommonConfiguration {

    private final Environment environment;

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    @Qualifier("activeProfile")
    public String activeProfile() {
        return environment.getActiveProfiles()[0];
    }

    @Bean
    public Gson gson(){
        return new Gson();
    }
}
