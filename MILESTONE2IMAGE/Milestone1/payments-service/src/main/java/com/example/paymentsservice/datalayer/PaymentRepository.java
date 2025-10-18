package com.example.paymentsservice.datalayer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByPaymentId(String paymentId);
}
