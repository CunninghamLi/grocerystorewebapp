package com.example.apigateway.presentationlayer.payment;

import com.example.apigateway.utils.GlobalControllerExceptionHandler;
import com.example.apigateway.utils.HttpErrorInfo;
import com.example.apigateway.utils.exceptions.InvalidInputException;
import com.example.apigateway.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.MockitoAnnotations.openMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.*;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PaymentControllerUnitTest {

    @Mock
    private RestTemplate restTemplate;

    // Using real ObjectMapper as it's crucial for exception handling logic in the controller
    private ObjectMapper realObjectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;
    private JacksonTester<PaymentRequestModel> jsonPaymentRequest;
    private JacksonTester<PaymentResponseModel> jsonPaymentResponse;


    private String paymentServiceBaseUrl;

    @Value("${app.payment-service.host}")
    private String host = "localhost"; // Default for test if not injected

    @Value("${app.payment-service.port}")
    private String port = "7002"; // Default for test if not injected


    @BeforeEach
    void setUp() {
        openMocks(this);
        JacksonTester.initFields(this, realObjectMapper);

        // Manually construct the base URL as @Value won't work directly here for the controller's fields
        // when it's instantiated with @InjectMocks. We set the fields directly in the controller instance.
        paymentServiceBaseUrl = "http://" + host + ":" + port + "/api/v1/payments";
        paymentController = new PaymentController(restTemplate, realObjectMapper, host, port);


        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalControllerExceptionHandler())
                .build();
    }

    @Test
    void getAllPayments_shouldReturnOk() throws Exception {
        PaymentResponseModel payment1 = PaymentResponseModel.builder().paymentId("pay1").amount("100.00").build();
        PaymentResponseModel payment2 = PaymentResponseModel.builder().paymentId("pay2").amount("200.00").build();
        List<PaymentResponseModel> payments = Arrays.asList(payment1, payment2);

        when(restTemplate.getForEntity(eq(paymentServiceBaseUrl), eq(PaymentResponseModel[].class)))
                .thenReturn(new ResponseEntity<>(payments.toArray(new PaymentResponseModel[0]), HttpStatus.OK));

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentId").value("pay1"))
                .andExpect(jsonPath("$[1].paymentId").value("pay2"));

        verify(restTemplate, times(1)).getForEntity(eq(paymentServiceBaseUrl), eq(PaymentResponseModel[].class));
    }

    @Test
    void getPaymentById_whenPaymentExists_shouldReturnOk() throws Exception {
        PaymentResponseModel payment = PaymentResponseModel.builder().paymentId("pay1").amount("100.00").build();
        String url = paymentServiceBaseUrl + "/pay1";
        when(restTemplate.getForObject(eq(url), eq(PaymentResponseModel.class))).thenReturn(payment);

        mockMvc.perform(get("/api/v1/payments/pay1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("pay1"));
        verify(restTemplate, times(1)).getForObject(eq(url), eq(PaymentResponseModel.class));
    }

    @Test
    void getPaymentById_whenPaymentNotFound_shouldReturnNotFound() throws Exception {
        String paymentId = "nonExistentId";
        String url = paymentServiceBaseUrl + "/" + paymentId;
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/payments/" + paymentId, "Payment not found");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, realObjectMapper.writeValueAsBytes(errorInfo), null);

        when(restTemplate.getForObject(eq(url), eq(PaymentResponseModel.class))).thenThrow(ex);

        mockMvc.perform(get("/api/v1/payments/" + paymentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Payment not found"));
        verify(restTemplate, times(1)).getForObject(eq(url), eq(PaymentResponseModel.class));
    }

    @Test
    void getPaymentById_whenOtherClientError_shouldThrowOriginalException() throws Exception {
        String paymentId = "errorId";
        String url = paymentServiceBaseUrl + "/" + paymentId;
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY, "{}".getBytes(), null);

        when(restTemplate.getForObject(eq(url), eq(PaymentResponseModel.class))).thenThrow(ex);

        mockMvc.perform(get("/api/v1/payments/" + paymentId))
                .andExpect(status().isBadRequest()); // Assuming GlobalExceptionHandler handles generic HttpClientErrorExceptions or rethrows
        verify(restTemplate, times(1)).getForObject(eq(url), eq(PaymentResponseModel.class));
    }


    @Test
    void createPayment_withValidData_shouldReturnOk() throws Exception {
        PaymentRequestModel requestModel = PaymentRequestModel.builder().amount("150.00").method("CREDIT_CARD").currency("USD").paymentDate(LocalDate.now()).build();
        PaymentResponseModel responseModel = PaymentResponseModel.builder().paymentId("payNew").amount("150.00").build();

        when(restTemplate.postForObject(eq(paymentServiceBaseUrl), any(PaymentRequestModel.class), eq(PaymentResponseModel.class)))
                .thenReturn(responseModel);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPaymentRequest.write(requestModel).getJson()))
                .andExpect(status().isOk()) // Controller returns the response from postForObject
                .andExpect(jsonPath("$.paymentId").value("payNew"));
        verify(restTemplate, times(1)).postForObject(eq(paymentServiceBaseUrl), any(PaymentRequestModel.class), eq(PaymentResponseModel.class));
    }

    @Test
    void createPayment_whenClientReturnsError_shouldPropagate() throws Exception {
        PaymentRequestModel requestModel = PaymentRequestModel.builder().amount("0").build(); // Potentially invalid
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "/api/v1/payments", "Invalid payment amount");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", HttpHeaders.EMPTY, realObjectMapper.writeValueAsBytes(errorInfo), null);

        when(restTemplate.postForObject(eq(paymentServiceBaseUrl), any(PaymentRequestModel.class), eq(PaymentResponseModel.class)))
                .thenThrow(ex);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPaymentRequest.write(requestModel).getJson()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(containsString("Invalid payment amount"))); // Message from HttpErrorInfo

        verify(restTemplate, times(1)).postForObject(eq(paymentServiceBaseUrl), any(PaymentRequestModel.class), eq(PaymentResponseModel.class));
    }

    @Test
    void updatePayment_whenPaymentExistsAndValidData_shouldReturnOk() throws Exception {
        String paymentId = "pay1";
        PaymentRequestModel requestModel = PaymentRequestModel.builder().amount("120.00").build();
        PaymentResponseModel updatedResponseModel = PaymentResponseModel.builder().paymentId(paymentId).amount("120.00").build();
        String urlPut = paymentServiceBaseUrl + "/" + paymentId;
        String urlGet = paymentServiceBaseUrl + "/" + paymentId;

        doNothing().when(restTemplate).put(eq(urlPut), any(PaymentRequestModel.class));
        when(restTemplate.getForObject(eq(urlGet), eq(PaymentResponseModel.class))).thenReturn(updatedResponseModel);


        mockMvc.perform(put("/api/v1/payments/" + paymentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPaymentRequest.write(requestModel).getJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value("120.00"));
        verify(restTemplate, times(1)).put(eq(urlPut), any(PaymentRequestModel.class));
        verify(restTemplate, times(1)).getForObject(eq(urlGet), eq(PaymentResponseModel.class)); // Called by controller's getById
    }

    @Test
    void updatePayment_whenGetByIdFailsAfterPut_shouldReturnNotFound() throws Exception {
        String paymentId = "pay1";
        PaymentRequestModel requestModel = PaymentRequestModel.builder().amount("120.00").build();
        String urlPut = paymentServiceBaseUrl + "/" + paymentId;
        String urlGet = paymentServiceBaseUrl + "/" + paymentId;

        doNothing().when(restTemplate).put(eq(urlPut), any(PaymentRequestModel.class));
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/payments/" + paymentId, "Payment not found after update");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, realObjectMapper.writeValueAsBytes(errorInfo), null);
        when(restTemplate.getForObject(eq(urlGet), eq(PaymentResponseModel.class))).thenThrow(ex);

        mockMvc.perform(put("/api/v1/payments/" + paymentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPaymentRequest.write(requestModel).getJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Payment not found after update"));
        verify(restTemplate, times(1)).put(eq(urlPut), any(PaymentRequestModel.class));
        verify(restTemplate, times(1)).getForObject(eq(urlGet), eq(PaymentResponseModel.class));
    }

    @Test
    void updatePayment_whenPutFailsWithNotFound_shouldReturnNotFound() throws Exception {
        String paymentId = "nonExistentId";
        PaymentRequestModel requestModel = PaymentRequestModel.builder().amount("120.00").build();
        String urlPut = paymentServiceBaseUrl + "/" + paymentId;

        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/payments/" + paymentId, "Payment not found for update");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, realObjectMapper.writeValueAsBytes(errorInfo), null);

        doThrow(ex).when(restTemplate).put(eq(urlPut), any(PaymentRequestModel.class));

        mockMvc.perform(put("/api/v1/payments/" + paymentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPaymentRequest.write(requestModel).getJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Payment not found for update"));
        verify(restTemplate, times(1)).put(eq(urlPut), any(PaymentRequestModel.class));
        verify(restTemplate, never()).getForObject(anyString(), eq(PaymentResponseModel.class)); // getById should not be called
    }


    @Test
    void deletePayment_whenPaymentExists_shouldReturnNoContent() throws Exception {
        String paymentId = "pay1";
        String url = paymentServiceBaseUrl + "/" + paymentId;
        doNothing().when(restTemplate).delete(eq(url));

        mockMvc.perform(delete("/api/v1/payments/" + paymentId))
                .andExpect(status().isNoContent());
        verify(restTemplate, times(1)).delete(eq(url));
    }

    @Test
    void deletePayment_whenPaymentNotFound_shouldReturnNotFound() throws Exception {
        String paymentId = "nonExistentId";
        String url = paymentServiceBaseUrl + "/" + paymentId;
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/payments/" + paymentId, "Payment to delete not found");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, realObjectMapper.writeValueAsBytes(errorInfo), null);

        doThrow(ex).when(restTemplate).delete(eq(url));

        mockMvc.perform(delete("/api/v1/payments/" + paymentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Payment to delete not found"));
        verify(restTemplate, times(1)).delete(eq(url));
    }
}