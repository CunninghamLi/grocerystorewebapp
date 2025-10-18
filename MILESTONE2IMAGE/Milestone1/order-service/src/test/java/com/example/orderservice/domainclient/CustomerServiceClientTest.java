package com.example.orderservice.domainclient;

import com.example.orderservice.presentationlayer.customer.CustomerResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(
        classes = { CustomerServiceClient.class, DomainClientTestConfig.class }
)
class CustomerServiceClientTest {

    @Autowired RestTemplate restTemplate;
    @Autowired CustomerServiceClient client;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getCustomer_success() {
        String json = """
      {
        "customerId":"42",
        "firstName":"Alice",
        "lastName":"Wonder"
      }
      """;

        // note the "/api/v1/customers/42" path and ":80" port
        server.expect(requestTo("http://test-host:80/api/v1/customers/42"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        CustomerResponseModel cust = client.getCustomerByCustomerId("42");

        assertThat(cust.getCustomerId()).isEqualTo("42");
        assertThat(cust.getFirstName()).isEqualTo("Alice");
        assertThat(cust.getLastName()).isEqualTo("Wonder");

        server.verify();
    }

    @Test
    void getCustomer_404_parsesError() {
        String err = "{ \"message\":\"Not Found\" }";

        server.expect(requestTo("http://test-host:80/api/v1/customers/99"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(err));

        Throwable ex = catchThrowable(() -> client.getCustomerByCustomerId("99"));
        assertThat(ex)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Not Found");

        server.verify();
    }
}
