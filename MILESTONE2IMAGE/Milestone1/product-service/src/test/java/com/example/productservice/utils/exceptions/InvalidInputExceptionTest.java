package com.example.productservice.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidInputExceptionTest {

    @Test
    void noArgConstructor_shouldHaveNullMessageAndCause() {
        InvalidInputException ex = new InvalidInputException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void messageConstructor_shouldSetMessageOnly() {
        InvalidInputException ex = new InvalidInputException("bad payload");
        assertEquals("bad payload", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void causeConstructor_shouldSetCauseOnly() {
        Throwable cause = new RuntimeException("root");
        InvalidInputException ex = new InvalidInputException(cause);
        assertSame(cause, ex.getCause());
        // message usually includes cause.toString(), but at minimum not null:
        assertTrue(ex.getMessage().contains("java.lang.RuntimeException: root"));
    }

    @Test
    void messageAndCauseConstructor_shouldSetBoth() {
        Throwable cause = new IllegalArgumentException("illegal");
        InvalidInputException ex = new InvalidInputException("oops", cause);
        assertEquals("oops", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
