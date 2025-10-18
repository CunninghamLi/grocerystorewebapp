package com.example.apigateway.presentationlayer.order;

public enum OrderStatus {
    PENDING,     // Or map RECEIVED to PENDING if that makes sense in your domain
    PROCESSING,
    SHIPPED,     // Or map ON_THE_WAY to SHIPPED
    DELIVERED,
    CANCELLED,
    COMPLETED,
    RECEIVED,    // New
    ON_THE_WAY
}