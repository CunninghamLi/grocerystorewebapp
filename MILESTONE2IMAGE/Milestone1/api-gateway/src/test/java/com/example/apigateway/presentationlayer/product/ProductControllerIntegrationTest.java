// src/test/java/com/example/apigateway/presentationlayer/product/ProductControllerIntegrationTest.java
package com.example.apigateway.presentationlayer.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createProduct_withValidData_shouldReturnOk() throws Exception {
        String json = "{\"name\":\"Widget\",\"price\":9.99}";
        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void createProduct_withInvalidData_shouldReturnUnprocessableEntity() throws Exception {
        String json = "{\"name\":\"\",\"price\":-1}";
        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getAllProducts_whenNoProducts_shouldReturnOkWithEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllProducts_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getProductById_whenProductExists_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", "prod1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("prod1"));
    }

    @Test
    void getProductById_whenProductNotFound_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", "nope"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct_withValidData_shouldReturnOk() throws Exception {
        String json = "{\"name\":\"Gadget\",\"price\":19.99}";
        mockMvc.perform(put("/api/v1/products/{id}", "prod1")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void updateProduct_withInvalidData_shouldReturnUnprocessableEntity() throws Exception {
        String json = "{\"name\":\"\",\"price\":-5}";
        mockMvc.perform(put("/api/v1/products/{id}", "prod1")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateProduct_whenProductNotFound_shouldReturnNotFound() throws Exception {
        String json = "{\"name\":\"X\",\"price\":1}";
        mockMvc.perform(put("/api/v1/products/{id}", "nope")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_whenProductExists_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", "prod1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_whenProductNotFound_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", "nope"))
                .andExpect(status().isNotFound());
    }
}
