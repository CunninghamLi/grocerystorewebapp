package com.example.paymentsservice.datalayer;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
public class Payment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(name = "payment_id", nullable = false)
        private String paymentId;

        @Column(name = "amount", nullable = false)
        private String amount;

        @Column(name = "method", nullable = false)
        private String method;

        @Column(name = "currency", nullable = false)
        private String currency;

        @Column(name = "payment_date", nullable = false)
        private LocalDate paymentDate;

        // Getters and setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }

        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public LocalDate getPaymentDate() { return paymentDate; }
        public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
}
