package com.example.orderservice.datalayer;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class OrderIdentifierTest {

    @Test
    void defaultConstructor_generatesValidUuidString() {
        OrderIdentifier identifier = new OrderIdentifier();

        // The generated ID should not be null or empty
        assertThat(identifier.getOrderId()).isNotBlank();

        // And it should parse as a valid UUID
        assertThatCode(() -> UUID.fromString(identifier.getOrderId()))
                .doesNotThrowAnyException();
    }

    @Test
    void stringConstructor_setsOrderIdExactly() {
        String customId = "my-special-id-123";
        OrderIdentifier identifier = new OrderIdentifier(customId);

        // Should return exactly what we passed in
        assertThat(identifier.getOrderId()).isEqualTo(customId);
    }
}
