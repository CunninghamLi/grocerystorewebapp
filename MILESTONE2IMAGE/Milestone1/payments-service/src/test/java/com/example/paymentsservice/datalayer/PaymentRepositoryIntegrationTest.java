package com.example.paymentsservice.datalayer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PaymentRepositoryIntegrationTest {

    @Autowired
    private PaymentRepository repository;

    @Test
    public void testSaveAndFindByPaymentId() {
        Payment payment = new Payment();
        payment.setPaymentId("PAY-001");
        payment.setAmount("200.00");
        payment.setMethod("Bank Transfer");
        payment.setCurrency("EUR");
        payment.setPaymentDate(LocalDate.of(2025, 5, 3));

        repository.save(payment);

        Optional<Payment> found = repository.findByPaymentId("PAY-001");

        assertTrue(found.isPresent());
        assertEquals("Bank Transfer", found.get().getMethod());
        assertEquals("200.00", found.get().getAmount());
    }

    @Test
    public void testFindByPaymentId_notFound() {
        Optional<Payment> result = repository.findByPaymentId("NOT-FOUND");
        assertFalse(result.isPresent());
    }
}
