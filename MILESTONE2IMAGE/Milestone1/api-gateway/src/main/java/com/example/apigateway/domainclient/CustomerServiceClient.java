// src/main/java/com/example/apigateway/domainclient/CustomerServiceClient.java
package com.example.apigateway.domainclient;

import com.example.apigateway.presentationlayer.customer.CustomerRequestModel;
import com.example.apigateway.presentationlayer.customer.CustomerResponseModel;
import com.example.apigateway.utils.HttpErrorInfo;
import com.example.apigateway.utils.exceptions.InvalidInputException;
import com.example.apigateway.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder; // Added for getCustomersByPhone

import java.util.Arrays;
import java.util.List;
import java.util.Map; // Added for getCustomersByPhone

@Slf4j
@Component
public class CustomerServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public CustomerServiceClient(RestTemplate restTemplate,
                                 ObjectMapper mapper,
                                 @Value("${app.customer-service.host}") String host,
                                 @Value("${app.customer-service.port}") String port) {
        this.restTemplate = restTemplate;
        this.mapper       = mapper;
        this.baseUrl      = "http://" + host + ":" + port + "/api/v1/customers";
    }

    public List<CustomerResponseModel> getAllCustomers() {
        var resp = restTemplate.getForEntity(baseUrl, CustomerResponseModel[].class);
        return Arrays.asList(resp.getBody());
    }

    public CustomerResponseModel getCustomerById(String id) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + id, CustomerResponseModel.class);
        } catch (HttpClientErrorException ex) {
            handleException(ex);
            return null;
        }
    }

    public CustomerResponseModel addCustomer(CustomerRequestModel req) {
        return restTemplate.postForObject(baseUrl, req, CustomerResponseModel.class);
    }

    public CustomerResponseModel updateCustomer(String id, CustomerRequestModel req) {
        try {
            restTemplate.put(baseUrl + "/" + id, req);
            return getCustomerById(id); // Assuming you want to return the updated customer
        } catch (HttpClientErrorException ex) {
            handleException(ex);
            return null;
        }
    }

    // Renamed from deleteCustomer to deleteCustomerById to match the controller
    public void deleteCustomerById(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (HttpClientErrorException ex) {
            handleException(ex);
        }
    }

    // Added method to get customer by email
    public CustomerResponseModel getCustomerByEmail(String email) {
        String url = baseUrl + "/email/" + email;
        try {
            return restTemplate.getForObject(url, CustomerResponseModel.class);
        } catch (HttpClientErrorException ex) {
            handleException(ex);
            return null;
        }
    }

    // Added method to delete customer by email
    public void deleteCustomerByEmail(String email) {
        String url = baseUrl + "/email/" + email;
        try {
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            handleException(ex);
        }
    }

    // Added method to get customers by phone
    // Note: The controller passes a Map<String, String> allParams which becomes String[] in the original call.
    // This implementation assumes the customer service endpoint for phone search is /by-phone
    // and can handle multiple phone numbers as query parameters.
    // You might need to adjust the parameter handling depending on your actual customer service API.
    public List<CustomerResponseModel> getCustomersByPhone(String country, String... phoneNumbers) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/by-phone")
                .queryParam("country", country);

        // Assuming phone numbers are passed as separate query parameters e.g., phone=123&phone=456
        // Adjust if your microservice expects a different format (e.g., comma-separated list)
        if (phoneNumbers != null) {
            for (String phoneNumber : phoneNumbers) {
                builder.queryParam("phone", phoneNumber); // Or adjust key if different
            }
        }

        String url = builder.toUriString();
        log.debug("Calling getCustomersByPhone with URL: {}", url);

        try {
            CustomerResponseModel[] response = restTemplate.getForObject(url, CustomerResponseModel[].class);
            return response != null ? Arrays.asList(response) : List.of();
        } catch (HttpClientErrorException ex) {
            handleException(ex);
            return List.of(); // Return empty list on error
        }
    }

    private void handleException(HttpClientErrorException ex) {
        HttpStatusCode status = ex.getStatusCode();
        String body = ex.getResponseBodyAsString();

        try {
            HttpErrorInfo info = mapper.readValue(body, HttpErrorInfo.class);
            if (status == HttpStatus.NOT_FOUND) {
                throw new NotFoundException(info.getMessage());
            } else if (status == HttpStatus.UNPROCESSABLE_ENTITY) {
                throw new InvalidInputException(info.getMessage());
            }
        } catch (Exception e) {
            log.error("Error parsing error info", e);
        }

        // fallback
        if (status == HttpStatus.NOT_FOUND) {
            throw new NotFoundException("Not found");
        } else if (status == HttpStatus.UNPROCESSABLE_ENTITY) {
            throw new InvalidInputException("Invalid input");
        }
        throw ex;
    }
}