package com.example.paymentsservice.utils.exceptions;

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
        InvalidInputException ex = new InvalidInputException("bad data");
        assertEquals("bad data", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void causeConstructor_shouldSetCauseOnly() {
        RuntimeException cause = new RuntimeException("oops");
        InvalidInputException ex = new InvalidInputException(cause);
        assertSame(cause, ex.getCause());
        assertEquals(cause.toString(), ex.getMessage());
    }

    @Test
    void messageAndCauseConstructor_shouldSetBoth() {
        RuntimeException cause = new RuntimeException("oops");
        InvalidInputException ex = new InvalidInputException("bad data", cause);
        assertEquals("bad data", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
