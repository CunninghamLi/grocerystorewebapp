package com.example.orderservice.domainclient;

import com.example.orderservice.presentationlayer.customer.CustomerResponseModel;
import com.example.orderservice.utils.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Component
public class CustomerServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String customerServiceBaseUrl;

    public CustomerServiceClient(RestTemplate restTemplate,
                                 ObjectMapper mapper,
                                 @Value("${app.customer-service.host}") String customerServiceHost,
                                 @Value("${app.customer-service.port}") String customerServicePort) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.customerServiceBaseUrl = "http://" + customerServiceHost + ":" + customerServicePort + "/api/v1/customers";
    }

    public CustomerResponseModel getCustomerByCustomerId(String customerId) {
        try {
            String url = String.format("%s/%s", customerServiceBaseUrl, customerId);
            return restTemplate.getForObject(url, CustomerResponseModel.class);
        } catch (HttpClientErrorException ex) {
            log.error("Error fetching customer: {}", customerId, ex);
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        try {
            HttpErrorInfo errorInfo = mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class);
            return new RuntimeException(errorInfo.getMessage());
        } catch (IOException ioex) {
            log.error("Error parsing error response", ioex);
            return new RuntimeException("Unknown error occurred");
        }
    }
}
