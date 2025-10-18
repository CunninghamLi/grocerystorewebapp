// src/main/java/com/example/apigateway/presentationlayer/order/OrderResponseModel.java
package com.example.apigateway.presentationlayer.order;


import com.example.apigateway.presentationlayer.customer.CustomerResponseModel;
import com.example.apigateway.presentationlayer.product.ProductResponseModel;
import com.example.apigateway.presentationlayer.payment.PaymentResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseModel extends RepresentationModel<OrderResponseModel> {
    private String orderId;
    private String customerId;
    private String paymentId;
    private List<String> productIds;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private Double amount;
    private String customerFirstName;
    private String customerLastName;
    private PaymentResponseModel payment;
    private CustomerResponseModel customer;
    private List<ProductResponseModel> products;
}
