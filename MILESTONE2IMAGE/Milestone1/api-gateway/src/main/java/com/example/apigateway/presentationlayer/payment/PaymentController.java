package com.example.apigateway.presentationlayer.payment;

import com.example.apigateway.utils.HttpErrorInfo;
import com.example.apigateway.utils.exceptions.InvalidInputException;
import com.example.apigateway.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String paymentServiceBaseUrl;

    public PaymentController(RestTemplate restTemplate,
                             ObjectMapper mapper,
                             @Value("${app.payment-service.host}") String host,
                             @Value("${app.payment-service.port}") String port) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.paymentServiceBaseUrl = "http://" + host + ":" + port + "/api/v1/payments";
    }

    @GetMapping
    public List<PaymentResponseModel> getAll() {
        var resp = restTemplate.getForEntity(paymentServiceBaseUrl, PaymentResponseModel[].class);
        return Arrays.asList(resp.getBody());
    }

    @GetMapping("/{paymentId}")
    public PaymentResponseModel getById(@PathVariable String paymentId) {
        try {
            return restTemplate.getForObject(
                    paymentServiceBaseUrl + "/" + paymentId,
                    PaymentResponseModel.class
            );
        } catch (HttpClientErrorException ex) {
            handleHttpClientException(ex);
            return null; // unreachable
        }
    }

    @PostMapping
    public PaymentResponseModel create(@RequestBody PaymentRequestModel body) {
        return restTemplate.postForObject(
                paymentServiceBaseUrl,
                body,
                PaymentResponseModel.class
        );
    }

    @PutMapping("/{paymentId}")
    public PaymentResponseModel update(@PathVariable String paymentId,
                                       @RequestBody PaymentRequestModel body) {
        try {
            restTemplate.put(paymentServiceBaseUrl + "/" + paymentId, body);
            return getById(paymentId);
        } catch (HttpClientErrorException ex) {
            handleHttpClientException(ex);
            return null;
        }
    }

    @DeleteMapping("/{paymentId}")
    @ResponseStatus(code = org.springframework.http.HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String paymentId) {
        try {
            restTemplate.delete(paymentServiceBaseUrl + "/" + paymentId);
        } catch (HttpClientErrorException ex) {
            handleHttpClientException(ex);
        }
    }

    private void handleHttpClientException(HttpClientErrorException ex) {
        HttpStatusCode status = ex.getStatusCode();
        String body = ex.getResponseBodyAsString();
        try {
            HttpErrorInfo info = mapper.readValue(body, HttpErrorInfo.class);
            if (status.equals(org.springframework.http.HttpStatus.NOT_FOUND)) {
                throw new NotFoundException(info.getMessage());
            } else if (status.equals(org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY)) {
                throw new InvalidInputException(info.getMessage());
            }
        } catch (Exception mapEx) {
            log.error("Failed to parse error response: {}", body, mapEx);
        }
        if (status.equals(org.springframework.http.HttpStatus.NOT_FOUND)) {
            throw new NotFoundException("Not found");
        } else if (status.equals(org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY)) {
            throw new InvalidInputException("Invalid input");
        } else {
            throw ex;
        }
    }
}
