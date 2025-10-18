package com.example.customerservice.utils.exceptions;

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
        InvalidInputException ex = new InvalidInputException("bad-input");
        assertEquals("bad-input", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void causeConstructor_shouldSetCauseOnly() {
        NumberFormatException cause = new NumberFormatException("not a number");
        InvalidInputException ex = new InvalidInputException(cause);
        assertSame(cause, ex.getCause());
    }

    @Test
    void messageAndCauseConstructor_shouldSetBoth() {
        Throwable cause = new RuntimeException("err");
        InvalidInputException ex = new InvalidInputException("oops", cause);
        assertEquals("oops", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
