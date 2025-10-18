package com.example.paymentsservice.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void noArgConstructor_shouldHaveNullMessageAndCause() {
        NotFoundException ex = new NotFoundException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void messageConstructor_shouldSetMessageOnly() {
        NotFoundException ex = new NotFoundException("not found");
        assertEquals("not found", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void causeConstructor_shouldSetCauseOnly() {
        Throwable cause = new IllegalStateException("gone");
        NotFoundException ex = new NotFoundException(cause);
        assertSame(cause, ex.getCause());
        assertEquals(cause.toString(), ex.getMessage());
    }

    @Test
    void messageAndCauseConstructor_shouldSetBoth() {
        Throwable cause = new IllegalArgumentException("bad id");
        NotFoundException ex = new NotFoundException("missing", cause);
        assertEquals("missing", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
