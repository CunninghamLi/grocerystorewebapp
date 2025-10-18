package com.example.productservice.presentationlayer;

import com.example.productservice.datalayer.Product;
import com.example.productservice.datalayer.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ProductControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    private Product savedProduct;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
        Product product = Product.builder()
                .productId("prod-001")
                .name("Sample Product")
                .description("This is a test product.")
                .price(29.99)
                .stockQuantity(100)
                .build();
        savedProduct = productRepository.save(product);
    }

    @Test
    void whenGettingAllProducts_thenCorrectResponse() {
        webTestClient.get().uri("/api/v1/products")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .hasSize((int) productRepository.count());
    }

    @Test
    void whenGettingProductById_thenReturnCorrectProduct() {
        webTestClient.get()
                .uri("/api/v1/products/{productId}", savedProduct.getProductId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(savedProduct.getName())
                .jsonPath("$.price").isEqualTo(savedProduct.getPrice());
    }

    @Test
    void whenGettingNonExistingProduct_thenReturnNotFound() {
        String nonExistentId = "prod-" + UUID.randomUUID();

        webTestClient.get()
                .uri("/api/v1/products/{productId}", nonExistentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
