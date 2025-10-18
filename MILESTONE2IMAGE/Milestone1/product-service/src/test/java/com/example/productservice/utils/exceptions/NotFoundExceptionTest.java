package com.example.productservice.utils.exceptions;

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
        NotFoundException ex = new NotFoundException("not there");
        assertEquals("not there", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void causeConstructor_shouldSetCauseOnly() {
        Throwable cause = new RuntimeException("missing");
        NotFoundException ex = new NotFoundException(cause);
        assertSame(cause, ex.getCause());
        assertTrue(ex.getMessage().contains("java.lang.RuntimeException: missing"));
    }

    @Test
    void messageAndCauseConstructor_shouldSetBoth() {
        Throwable cause = new IllegalArgumentException("bad id");
        NotFoundException ex = new NotFoundException("404", cause);
        assertEquals("404", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
