// src/test/java/com/example/apigateway/presentationlayer/order/OrderControllerIntegrationTest.java
package com.example.apigateway.presentationlayer.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createOrder_withValidData_shouldReturnOk() throws Exception {
        String json = "{"
                + "\"customerId\":\"cust123\","
                + "\"paymentId\":\"pay123\","
                + "\"productIds\":[\"p1\",\"p2\"],"
                + "\"amount\":42.50"
                + "}";
        mockMvc.perform(post("/api/v1/orders")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void createOrder_withInvalidData_shouldReturnUnprocessableEntity() throws Exception {
        String json = "{\"customerId\":null,\"paymentId\":\"p\",\"productIds\":[],\"amount\":-1}";
        mockMvc.perform(post("/api/v1/orders")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getAllOrders_whenNoOrders_shouldReturnOkWithEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllOrders_shouldReturnOk() throws Exception {
        // pre-load one order via repository or service...
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getOrderById_whenOrderExists_shouldReturnOk() throws Exception {
        String id = "order1";
        // assume order1 exists
        mockMvc.perform(get("/api/v1/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderIdentifier").value(id));
    }

    @Test
    void getOrderById_whenOrderNotFound_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/orders/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrder_withValidData_shouldReturnOk() throws Exception {
        String id = "order1";
        String json = "{\"customerId\":\"newCust\",\"paymentId\":\"newPay\",\"productIds\":[\"p3\"],\"amount\":10}";
        mockMvc.perform(put("/api/v1/orders/{id}", id)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void updateOrder_withInvalidData_shouldReturnUnprocessableEntity() throws Exception {
        String id = "order1";
        String json = "{\"customerId\":null,\"paymentId\":\"p\",\"productIds\":[],\"amount\":-5}";
        mockMvc.perform(put("/api/v1/orders/{id}", id)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateOrder_whenOrderNotFound_shouldReturnNotFound() throws Exception {
        String json = "{\"customerId\":\"c\",\"paymentId\":\"p\",\"productIds\":[\"p\"],\"amount\":5}";
        mockMvc.perform(put("/api/v1/orders/{id}", "nope")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrder_whenOrderExists_shouldReturnOk() throws Exception {
        String id = "order1";
        mockMvc.perform(delete("/api/v1/orders/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void deleteOrder_whenOrderNotFound_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/{id}", "nope"))
                .andExpect(status().isNotFound());
    }
}
