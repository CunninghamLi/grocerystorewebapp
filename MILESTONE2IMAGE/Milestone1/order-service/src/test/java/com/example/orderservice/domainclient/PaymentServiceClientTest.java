package com.example.orderservice.domainclient;

import com.example.orderservice.presentationlayer.payment.PaymentResponseModel;
import com.example.orderservice.utils.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(
        classes = { PaymentServiceClient.class, DomainClientTestConfig.class }
)
@TestPropertySource(properties = {
        "app.payment-service.host=pay-host",
        "app.payment-service.port=80"
})
class PaymentServiceClientTest {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    PaymentServiceClient client;
    private MockRestServiceServer server;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void before() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getPayment_success() {
        String json = """
      {
        "paymentId":"PMT123",
        "amount":"50.00",
        "method":"Card",
        "currency":"USD",
        "paymentDate":"2025-05-14"
      }
      """;

        server.expect(requestTo("http://pay-host:80/api/v1/payments/PMT123"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        PaymentResponseModel pay = client.getPaymentById("PMT123");
        assertThat(pay.getPaymentId()).isEqualTo("PMT123");
        assertThat(pay.getAmount()).isEqualTo("50.00");

        server.verify();
    }
/*
    @Test
    void getPayment_4xxWithJson_parsesErrorMessage() throws Exception {
        HttpErrorInfo error = new HttpErrorInfo(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "/api/v1/payments/ERR",
                "Bad Data"
        );
        String body = mapper.writeValueAsString(error);

        server.expect(requestTo("http://pay-host:80/api/v1/payments/ERR"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withServerError()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                );

        Throwable ex = catchThrowable(() -> client.getPaymentById("ERR"));
        assertThat(ex)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bad Data");

        server.verify();
    }

    @Test
    void getPayment_500_parseFailure_returnsUnknown() {
        server.expect(requestTo("http://pay-host:80/api/v1/payments/ERR2"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withServerError()
                        .body("{not-json")
                );

        Throwable ex = catchThrowable(() -> client.getPaymentById("ERR2"));
        assertThat(ex)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unknown error occurred");

        server.verify();
    }*/

}
