// Order.java
package com.example.orderservice.datalayer;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Aggregate root: Order
 * Invariant: order.amount == sum(product.price for each productId)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private OrderIdentifier orderIdentifier;
    private String customerId;
    private String paymentId;
    private List<String> productIds;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private Double amount;

    public void validateBasicInvariant() {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalStateException("Order must contain at least one product");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalStateException("Order amount must be greater than zero");
        }
    }
}
