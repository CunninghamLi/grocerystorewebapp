package com.example.orderservice.domainclient;

import com.example.orderservice.presentationlayer.product.ProductResponseModel;
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
        classes = { ProductServiceClient.class, DomainClientTestConfig.class }
)
class ProductServiceClientTest {

    @Autowired RestTemplate restTemplate;
    @Autowired ProductServiceClient client;
    private MockRestServiceServer server;

    @BeforeEach
    void init() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getProduct_success() {
        String json = """
      {
        "productId":"p100",
        "name":"Gizmo",
        "description":"Cool",
        "price":9.99,
        "stockQuantity":100
      }
      """;

        server.expect(requestTo("http://prod-host:80/api/v1/products/p100"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        ProductResponseModel p = client.getProductByProductId("p100");
        assertThat(p.getProductId()).isEqualTo("p100");
        assertThat(p.getName()).isEqualTo("Gizmo");
        assertThat(p.getPrice()).isEqualTo(9.99);

        server.verify();
    }

    @Test
    void getProduct_400_unknownError() {
        server.expect(requestTo("http://prod-host:80/api/v1/products/xxx"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).body("oops"));

        Throwable ex = catchThrowable(() -> client.getProductByProductId("xxx"));
        assertThat(ex)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unknown error occurred");

        server.verify();
    }
}
