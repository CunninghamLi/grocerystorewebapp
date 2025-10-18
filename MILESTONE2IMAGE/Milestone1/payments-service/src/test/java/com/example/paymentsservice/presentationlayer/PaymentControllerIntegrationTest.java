package com.example.paymentsservice.presentationlayer;

import com.example.paymentsservice.datalayer.Payment;
import com.example.paymentsservice.datalayer.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private PaymentRepository repository;

    @BeforeEach
    public void setup() {
        repository.deleteAll();
        Payment p = new Payment();
        p.setPaymentId("PAY-200");
        p.setAmount("125.50");
        p.setMethod("Credit Card");
        p.setCurrency("USD");
        p.setPaymentDate(LocalDate.of(2025, 5, 1));
        repository.save(p);
    }

    @Test
    public void testGetAllPayments() throws Exception {
        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Credit Card")));
    }

    @Test
    public void testGetById_found() throws Exception {
        mockMvc.perform(get("/api/v1/payments/PAY-200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("Credit Card"));
    }

    @Test
    public void testGetById_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/payments/NOTFOUND"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreatePayment() throws Exception {
        String json = """
        {
          "paymentId": "PAY-201",
          "amount": "300.00",
          "method": "PayPal",
          "currency": "CAD",
          "paymentDate": "2025-05-02"
        }
        """;

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("PayPal"));
    }

    @Test
    public void testUpdatePayment() throws Exception {
        String json = """
        {
          "paymentId": "PAY-200",
          "amount": "150.00",
          "method": "UpdatedMethod",
          "currency": "USD",
          "paymentDate": "2025-05-01"
        }
        """;

        mockMvc.perform(put("/api/v1/payments/PAY-200")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("UpdatedMethod"));
    }

    @Test
    public void testDeletePayment() throws Exception {
        mockMvc.perform(delete("/api/v1/payments/PAY-200"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeletePayment_notFound() throws Exception {
        mockMvc.perform(delete("/api/v1/payments/UNKNOWN"))
                .andExpect(status().isNotFound());
    }
}
