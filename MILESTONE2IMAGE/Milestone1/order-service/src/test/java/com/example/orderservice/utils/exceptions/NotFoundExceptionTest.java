// src/test/java/com/example/orderservice/utils/exceptions/NotFoundExceptionTest.java
package com.example.orderservice.utils.exceptions;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void constructor_messageOnly_setsMessageAndNoCause() {
        NotFoundException ex = new NotFoundException("not here");
        assertThat(ex.getMessage()).isEqualTo("not here");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void constructor_messageAndCause_setsBoth() {
        IllegalArgumentException cause = new IllegalArgumentException("bad arg");
        NotFoundException ex = new NotFoundException("not here", cause);
        assertThat(ex.getMessage()).isEqualTo("not here");
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
