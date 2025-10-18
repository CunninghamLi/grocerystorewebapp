package com.example.apigateway.presentationlayer.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseModel {
    private Integer id;
    private String paymentId;
    private String amount;
    private String method;
    private String currency;
    private LocalDate paymentDate;
}
