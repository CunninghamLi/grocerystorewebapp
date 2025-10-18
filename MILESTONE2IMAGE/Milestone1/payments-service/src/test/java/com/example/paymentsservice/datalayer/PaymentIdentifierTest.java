package com.example.paymentsservice.datalayer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentIdentifierTest {

    @Test
    void defaultConstructor_generatesNonNullAndUniqueIds() {
        PaymentIdentifier id1 = new PaymentIdentifier();
        PaymentIdentifier id2 = new PaymentIdentifier();

        assertNotNull(id1.getPaymentId(), "Default constructor should generate a non-null ID");
        assertNotNull(id2.getPaymentId(), "Default constructor should generate a non-null ID");
        // Very unlikely, but just assert they're not the same
        assertNotEquals(id1.getPaymentId(), id2.getPaymentId(), "Two default IDs should differ");
    }

    @Test
    void parameterizedConstructor_setsGivenValue() {
        String expected = "CUSTOM-ID-123";
        PaymentIdentifier id = new PaymentIdentifier(expected);

        assertEquals(expected, id.getPaymentId(), "Parameterized constructor should set the given ID");
    }
}
