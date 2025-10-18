package com.example.orderservice.domainclient;

import com.example.orderservice.presentationlayer.payment.PaymentResponseModel;
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
public class PaymentServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String paymentServiceBaseUrl;

    public PaymentServiceClient(RestTemplate restTemplate,
                                ObjectMapper mapper,
                                @Value("${app.payment-service.host}") String paymentServiceHost,
                                @Value("${app.payment-service.port}") String paymentServicePort) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.paymentServiceBaseUrl = "http://" + paymentServiceHost + ":" + paymentServicePort + "/api/v1/payments";
    }

    public PaymentResponseModel getPaymentById(String paymentId) {
        try {
            String url = String.format("%s/%s", paymentServiceBaseUrl, paymentId);
            return restTemplate.getForObject(url, PaymentResponseModel.class);
        } catch (HttpClientErrorException ex) {
            log.error("Error fetching payment: {}", paymentId, ex);
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
