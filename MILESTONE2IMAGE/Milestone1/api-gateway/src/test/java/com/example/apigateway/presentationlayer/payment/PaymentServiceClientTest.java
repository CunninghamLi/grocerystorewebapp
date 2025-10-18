package com.example.apigateway.presentationlayer.payment;

import com.example.apigateway.domainclient.PaymentServiceClient;
import com.example.apigateway.presentationlayer.payment.PaymentRequestModel;
import com.example.apigateway.presentationlayer.payment.PaymentResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PaymentServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    // ObjectMapper is not directly used by PaymentServiceClient, but good for creating test objects
    private ObjectMapper objectMapper;

    private PaymentServiceClient paymentServiceClient;

    private final String paymentServiceHost = "localhost";
    // Port for PaymentServiceClient is int in constructor, but string in application.yml for other services.
    // Let's use the port from app.payment-service.port in application.yml (default profile) = 7002
    private final int paymentServicePort = 7002;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        baseUrl = "http://" + paymentServiceHost + ":" + paymentServicePort + "/api/v1/payments";
        paymentServiceClient = new PaymentServiceClient(restTemplate, paymentServiceHost, paymentServicePort);
    }

    @Test
    void getAllPayments_shouldReturnListOfPayments() {
        PaymentResponseModel payment1 = PaymentResponseModel.builder().paymentId("pay1").amount("100.00").build();
        PaymentResponseModel[] paymentsArray = {payment1};
        ResponseEntity<PaymentResponseModel[]> responseEntity = new ResponseEntity<>(paymentsArray, HttpStatus.OK);

        when(restTemplate.getForEntity(eq(baseUrl), eq(PaymentResponseModel[].class)))
                .thenReturn(responseEntity);

        List<PaymentResponseModel> result = paymentServiceClient.getAllPayments();

        assertEquals(1, result.size());
        assertEquals("pay1", result.get(0).getPaymentId());
        verify(restTemplate, times(1)).getForEntity(eq(baseUrl), eq(PaymentResponseModel[].class));
    }

    @Test
    void getAllPayments_whenServiceReturnsError_shouldPropagateException() {
        when(restTemplate.getForEntity(eq(baseUrl), eq(PaymentResponseModel[].class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", HttpHeaders.EMPTY, null, null));

        assertThrows(HttpClientErrorException.class, () -> {
            paymentServiceClient.getAllPayments();
        });
    }

    @Test
    void getPaymentById_whenPaymentExists_shouldReturnPayment() {
        String paymentId = "pay1";
        PaymentResponseModel expectedPayment = PaymentResponseModel.builder().paymentId(paymentId).build();
        String url = baseUrl + "/" + paymentId;

        when(restTemplate.getForObject(eq(url), eq(PaymentResponseModel.class))).thenReturn(expectedPayment);

        PaymentResponseModel actualPayment = paymentServiceClient.getPaymentById(paymentId);

        assertNotNull(actualPayment);
        assertEquals(paymentId, actualPayment.getPaymentId());
    }

    @Test
    void getPaymentById_whenNotFound_shouldPropagateHttpClientErrorException() {
        String paymentId = "payNonExistent";
        String url = baseUrl + "/" + paymentId;
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null);

        when(restTemplate.getForObject(eq(url), eq(PaymentResponseModel.class))).thenThrow(ex);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> {
            paymentServiceClient.getPaymentById(paymentId);
        });
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
    }

    @Test
    void addPayment_shouldReturnCreatedPayment() {
        PaymentRequestModel requestModel = PaymentRequestModel.builder().amount("50.00").method("CARD").build();
        PaymentResponseModel expectedResponse = PaymentResponseModel.builder().paymentId("payNew").amount("50.00").build();
        ResponseEntity<PaymentResponseModel> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

        when(restTemplate.postForEntity(eq(baseUrl), any(PaymentRequestModel.class), eq(PaymentResponseModel.class)))
                .thenReturn(responseEntity);

        PaymentResponseModel actualResponse = paymentServiceClient.addPayment(requestModel);

        assertNotNull(actualResponse);
        assertEquals("payNew", actualResponse.getPaymentId());
    }

    @Test
    void addPayment_whenServiceReturnsError_shouldPropagateException() {
        PaymentRequestModel requestModel = PaymentRequestModel.builder().amount("0").build();
        when(restTemplate.postForEntity(eq(baseUrl), any(PaymentRequestModel.class), eq(PaymentResponseModel.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Data", HttpHeaders.EMPTY, null, null));

        assertThrows(HttpClientErrorException.class, () -> {
            paymentServiceClient.addPayment(requestModel);
        });
    }


    @Test
    void updatePayment_shouldReturnUpdatedPayment() {
        String paymentId = "pay1";
        PaymentRequestModel requestModel = PaymentRequestModel.builder().amount("120.00").build();
        PaymentResponseModel expectedResponse = PaymentResponseModel.builder().paymentId(paymentId).amount("120.00").build();
        String url = baseUrl + "/" + paymentId;
        HttpEntity<PaymentRequestModel> requestEntity = new HttpEntity<>(requestModel);
        ResponseEntity<PaymentResponseModel> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.PUT), eq(requestEntity), eq(PaymentResponseModel.class)))
                .thenReturn(responseEntity);

        PaymentResponseModel actualResponse = paymentServiceClient.updatePayment(paymentId, requestModel);

        assertNotNull(actualResponse);
        assertEquals("120.00", actualResponse.getAmount());
    }

    @Test
    void updatePayment_whenServiceReturnsError_shouldPropagateException() {
        String paymentId = "pay1";
        PaymentRequestModel requestModel = PaymentRequestModel.builder().amount("error").build();
        String url = baseUrl + "/" + paymentId;
        HttpEntity<PaymentRequestModel> requestEntity = new HttpEntity<>(requestModel);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.PUT), eq(requestEntity), eq(PaymentResponseModel.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY, null, null));

        assertThrows(HttpClientErrorException.class, () -> {
            paymentServiceClient.updatePayment(paymentId, requestModel);
        });
    }


    @Test
    void deletePayment_shouldCallDeleteOnRestTemplate() {
        String paymentId = "pay1";
        String url = baseUrl + "/" + paymentId;

        doNothing().when(restTemplate).delete(eq(url));
        paymentServiceClient.deletePayment(paymentId);
        verify(restTemplate, times(1)).delete(eq(url));
    }

    @Test
    void deletePayment_whenServiceThrowsError_shouldPropagateException() {
        String paymentId = "payNonExistent";
        String url = baseUrl + "/" + paymentId;
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null);

        doThrow(ex).when(restTemplate).delete(eq(url));

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> {
            paymentServiceClient.deletePayment(paymentId);
        });
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
    }
}