// src/test/java/com/example/orderservice/domainclient/DomainClientTestConfig.java
package com.example.orderservice.domainclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class DomainClientTestConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
