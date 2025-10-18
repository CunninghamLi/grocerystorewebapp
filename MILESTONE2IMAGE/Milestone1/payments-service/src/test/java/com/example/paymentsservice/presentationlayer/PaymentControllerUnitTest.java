package com.example.paymentsservice.presentationlayer;

import com.example.paymentsservice.businesslayer.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentControllerUnitTest {

    private PaymentService service;
    private PaymentController controller;

    @BeforeEach
    void setup() {
        service = mock(PaymentService.class);
        controller = new PaymentController(service);
    }

    @Test
    void testGetAll() {
        when(service.getAllPayments()).thenReturn(Collections.emptyList());
        ResponseEntity<?> result = controller.getAll();
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void testDeleteCallsService() {
        controller.delete("PAY-X");
        verify(service).removePayment("PAY-X");
    }
}
