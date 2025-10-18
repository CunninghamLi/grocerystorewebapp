package com.example.paymentsservice.utils.exceptions;

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
        DuplicateVinException ex = new DuplicateVinException("dup");
        assertEquals("dup", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void causeConstructor_shouldSetCauseOnly() {
        Throwable cause = new IllegalStateException("boom");
        DuplicateVinException ex = new DuplicateVinException(cause);
        assertSame(cause, ex.getCause());
        // message will be cause.toString()
        assertEquals(cause.toString(), ex.getMessage());
    }

    @Test
    void messageAndCauseConstructor_shouldSetBoth() {
        Throwable cause = new IllegalArgumentException("bad");
        DuplicateVinException ex = new DuplicateVinException("dup", cause);
        assertEquals("dup", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
