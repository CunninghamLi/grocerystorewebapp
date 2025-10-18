package com.example.orderservice.presentationlayer;

import com.example.orderservice.datalayer.OrderStatus;
import jakarta.validation.constraints.NotNull; // Keep if you have validation
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC) // Ensure it's public if needed, or remove if builder is primary
public class OrderRequestModel {

    // Assuming customerId is provided when creating/updating an order
    private String customerId;
    private String paymentId; // Can be null if payment is processed later or not applicable

    @NotNull(message = "Product IDs list cannot be null")
    private List<String> productIds; // <--- MODIFIED FROM List<Integer>

    // Status and Amount might be set by the backend or optionally by the client
    private OrderStatus status;
    private Double amount; // This could be calculated by the backend based on products
}