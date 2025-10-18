package com.example.productservice.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateVinExceptionTest {

    @Test
    void noArgConstructor_shouldHaveNullMessageAndCause() {
        DuplicateVinException ex = new DuplicateVinException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void messageConstructor_shouldSetMessageOnly() {
        DuplicateVinException ex = new DuplicateVinException("dup VIN");
        assertEquals("dup VIN", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void causeConstructor_shouldSetCauseOnly() {
        Throwable cause = new RuntimeException("duplicate");
        DuplicateVinException ex = new DuplicateVinException(cause);
        assertSame(cause, ex.getCause());
        assertTrue(ex.getMessage().contains("java.lang.RuntimeException: duplicate"));
    }

    @Test
    void messageAndCauseConstructor_shouldSetBoth() {
        Throwable cause = new IllegalStateException("state");
        DuplicateVinException ex = new DuplicateVinException("dup error", cause);
        assertEquals("dup error", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
