package com.example.apigateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
// RestTemplate import is no longer needed here if the bean is removed
// import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = "com.example")
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    // The @Bean for RestTemplate has been removed from this file.
    // The definition in RestTemplateConfig.java will be used instead.

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register the module
        // Any other custom configurations for your ObjectMapper
        return objectMapper;
    }
}