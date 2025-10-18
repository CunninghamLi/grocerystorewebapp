// Modified API Gateway OrderRequestModel
package com.example.apigateway.presentationlayer.order;

import com.example.apigateway.presentationlayer.order.OrderStatus; // Ensure this is the correct OrderStatus for API Gateway
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Removed: import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestModel {
    private String customerId;
    private String paymentId;
    private List<String> productIds; // Changed to List<String>
    // private LocalDateTime createdAt; // Removed
    private OrderStatus status;
    private Double amount;
}