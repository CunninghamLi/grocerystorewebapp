package com.example.apigateway.domainclient;

import com.example.apigateway.presentationlayer.payment.PaymentRequestModel;
import com.example.apigateway.presentationlayer.payment.PaymentResponseModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class PaymentServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PaymentServiceClient(RestTemplate restTemplate,
                                @Value("${app.payment-service.host}") String host,
                                @Value("${app.payment-service.port}") int port) {
        this.restTemplate = restTemplate;
        this.baseUrl = String.format("http://%s:%d/api/v1/payments", host, port);
    }

    public List<PaymentResponseModel> getAllPayments() {
        ResponseEntity<PaymentResponseModel[]> resp =
                restTemplate.getForEntity(baseUrl, PaymentResponseModel[].class);
        return Arrays.asList(resp.getBody());
    }

    public PaymentResponseModel getPaymentById(String id) {
        return restTemplate.getForObject(baseUrl + "/" + id, PaymentResponseModel.class);
    }

    public PaymentResponseModel addPayment(PaymentRequestModel req) {
        return restTemplate.postForEntity(baseUrl, req, PaymentResponseModel.class).getBody();
    }

    public PaymentResponseModel updatePayment(String id, PaymentRequestModel req) {
        HttpEntity<PaymentRequestModel> ent = new HttpEntity<>(req);
        return restTemplate.exchange(baseUrl + "/" + id, HttpMethod.PUT, ent, PaymentResponseModel.class)
                .getBody();
    }

    public void deletePayment(String id) {
        restTemplate.delete(baseUrl + "/" + id);
    }
}
