package com.example.paymentsservice.datalayer;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;
@Embeddable
@Getter

public class PaymentIdentifier {



    private String paymentId;

        public PaymentIdentifier() {
            this.paymentId = UUID.randomUUID().toString();
        }

        public PaymentIdentifier(String customerId) {
            this.paymentId = customerId;
        }
    }

