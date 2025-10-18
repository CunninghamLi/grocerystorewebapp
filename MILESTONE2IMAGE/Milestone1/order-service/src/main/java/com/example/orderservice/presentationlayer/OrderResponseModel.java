package com.example.orderservice.presentationlayer;

import com.example.orderservice.datalayer.OrderStatus;
import com.example.orderservice.presentationlayer.customer.CustomerResponseModel;
import com.example.orderservice.presentationlayer.payment.PaymentResponseModel;
import com.example.orderservice.presentationlayer.product.ProductResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class OrderResponseModel extends RepresentationModel<OrderResponseModel> {
    private String orderId;
    private String customerId;
    private String paymentId;
    private List<String> productIds; // <--- ENSURE THIS IS List<String> if it was List<Integer>
    private LocalDateTime createdAt;
    private OrderStatus status;
    private Double amount;
    private String customerFirstName;
    private String customerLastName;

    private PaymentResponseModel payment;
    private CustomerResponseModel customer;
    private List<ProductResponseModel> products;
}