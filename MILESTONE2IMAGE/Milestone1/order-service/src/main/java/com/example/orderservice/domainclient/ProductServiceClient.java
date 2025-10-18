package com.example.orderservice.domainclient;

import com.example.orderservice.presentationlayer.product.ProductResponseModel;
import com.example.orderservice.utils.HttpErrorInfo; // Ensure this import is correct
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Component
public class ProductServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String productServiceBaseUrl;

    public ProductServiceClient(RestTemplate restTemplate,
                                ObjectMapper mapper,
                                @Value("${app.product-service.host}") String productServiceHost,
                                @Value("${app.product-service.port}") String productServicePort) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        // Ensure this URL points to an endpoint that accepts string product IDs in the path
        this.productServiceBaseUrl = "http://" + productServiceHost + ":" + productServicePort + "/api/v1/products";
    }

    // Method changed to accept String productId
    public ProductResponseModel getProductByProductId(String productIdString) { // <--- MODIFIED parameter to String
        try {
            String url = String.format("%s/%s", productServiceBaseUrl, productIdString); // productIdString is now like "p001"
            log.debug("Calling product service with URL: {}", url);
            return restTemplate.getForObject(url, ProductResponseModel.class);
        } catch (HttpClientErrorException ex) {
            log.error("Error fetching product: {} - Status: {}, Body: {}", productIdString, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw handleHttpClientException(ex);
        } catch (Exception ex) { // Catch broader exceptions
            log.error("Generic error fetching product: {} - Error: {}", productIdString, ex.getMessage());
            throw new RuntimeException("An unexpected error occurred while fetching product " + productIdString, ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        try {
            String responseBody = ex.getResponseBodyAsString();
            log.debug("Attempting to parse error response body: {}", responseBody);
            HttpErrorInfo errorInfo = mapper.readValue(responseBody, HttpErrorInfo.class);
            return new RuntimeException(errorInfo.getMessage());
        } catch (IOException ioex) {
            log.error("Error parsing error response for status {} and body {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ioex.getMessage());
            return new RuntimeException("Unknown error occurred parsing error response: " + ex.getResponseBodyAsString(), ioex);
        }
    }
}