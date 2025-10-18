package com.example.customerservice.utils.exceptions;

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
        NotFoundException ex = new NotFoundException("not-found");
        assertEquals("not-found", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void causeConstructor_shouldSetCauseOnly() {
        Exception cause = new Exception("inner");
        NotFoundException ex = new NotFoundException(cause);
        assertSame(cause, ex.getCause());
    }

    @Test
    void messageAndCauseConstructor_shouldSetBoth() {
        Exception cause = new Exception("inner");
        NotFoundException ex = new NotFoundException("nope", cause);
        assertEquals("nope", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
