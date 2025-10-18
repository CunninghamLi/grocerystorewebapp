package com.example.customerservice.datalayer;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class CustomerIdentifierTest {

    @Test
    void defaultConstructor_generatesValidUuid() {
        CustomerIdentifier id1 = new CustomerIdentifier();
        CustomerIdentifier id2 = new CustomerIdentifier();

        // Both IDs should be non-null and non-empty
        assertThat(id1.getCustomerId()).isNotBlank();
        assertThat(id2.getCustomerId()).isNotBlank();

        // They should parse as valid UUIDs
        assertThatCode(() -> UUID.fromString(id1.getCustomerId())).doesNotThrowAnyException();
        assertThatCode(() -> UUID.fromString(id2.getCustomerId())).doesNotThrowAnyException();

        // Two default instances should generate different IDs
        assertThat(id1.getCustomerId()).isNotEqualTo(id2.getCustomerId());
    }

    @Test
    void stringConstructor_setsExactCustomerId() {
        String fixedId = "customer-123";
        CustomerIdentifier identifier = new CustomerIdentifier(fixedId);

        // Getter should return exactly the string passed in
        assertThat(identifier.getCustomerId()).isEqualTo(fixedId);
    }
}
