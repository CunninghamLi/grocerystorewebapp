package com.example.productservice.datalayer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductIdentifierTest {

    @Test
    void defaultConstructor_generatesNonNullUuid() {
        ProductIdentifier id = new ProductIdentifier();
        assertNotNull(id.getProductId(), "default ctor should set a UUID");
        // Optionally: check format looks like UUID
        assertTrue(id.getProductId().matches("^[0-9a-fA-F\\-]{36}$"), "should be valid UUID format");
    }

    @Test
    void paramConstructor_setsGivenValue() {
        ProductIdentifier id = new ProductIdentifier("MY-ID-123");
        assertEquals("MY-ID-123", id.getProductId());
    }

    @Test
    void setter_updatesValue() {
        ProductIdentifier id = new ProductIdentifier("initial");
        id.setProductId("updated");
        assertEquals("updated", id.getProductId());
    }
}
