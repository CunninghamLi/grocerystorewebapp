// src/test/java/com/example/apigateway/presentationlayer/payment/PaymentControllerIntegrationTest.java
package com.example.apigateway.presentationlayer.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createPayment_withValidData_shouldReturnOk() throws Exception {
        String json = "{\"orderId\":\"ord1\",\"amount\":100.00}";
        mockMvc.perform(post("/api/v1/payments")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void createPayment_withInvalidDataFromService_shouldReturnUnprocessableEntity() throws Exception {
        String json = "{\"orderId\":null,\"amount\":-10}";
        mockMvc.perform(post("/api/v1/payments")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getAllPayments_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getPaymentById_whenPaymentExists_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/payments/{id}", "pay1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pay1"));
    }

    @Test
    void getPaymentById_whenPaymentNotFound_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/payments/{id}", "nope"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePayment_whenPaymentExistsAndValidData_shouldReturnOk() throws Exception {
        String json = "{\"orderId\":\"ord2\",\"amount\":50}";
        mockMvc.perform(put("/api/v1/payments/{id}", "pay1")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void deletePayment_whenPaymentExists_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/payments/{id}", "pay1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePayment_whenServiceThrowsNotFound_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/payments/{id}", "nope"))
                .andExpect(status().isNotFound());
    }
}
