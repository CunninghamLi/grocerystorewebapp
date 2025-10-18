
/*package com.example.orderservice.utils.exceptions;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class InvalidInputExceptionTest {

    @Test
    void constructor_messageOnly_setsMessageAndNoCause() {
        InvalidInputException ex = new InvalidInputException("bad data");
        assertThat(ex.getMessage()).isEqualTo("bad data");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void constructor_messageAndCause_setsBoth() {
        RuntimeException cause = new RuntimeException("root");
        InvalidInputException ex = new InvalidInputException("bad data", cause);
        assertThat(ex.getMessage()).isEqualTo("bad data");
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
*/
package com.example.orderservice.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InvalidInputExceptionTest {

    @Test
    void defaultConstructor_setsNoMessageAndNoCause() {
        InvalidInputException ex = new InvalidInputException();
        assertThat(ex.getMessage()).isNull();
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void causeConstructor_setsCauseAndUsesCauseToStringAsMessage() {
        Throwable cause = new IllegalArgumentException("boom");
        InvalidInputException ex = new InvalidInputException(cause);
        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex.getMessage()).isEqualTo(cause.toString());
    }

    @Test
    void constructor_messageOnly_setsMessageAndNoCause() {
        InvalidInputException ex = new InvalidInputException("bad data");
        assertThat(ex.getMessage()).isEqualTo("bad data");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void constructor_messageAndCause_setsBoth() {
        String msg = "bad data";
        Throwable cause = new RuntimeException("root");
        InvalidInputException ex = new InvalidInputException(msg, cause);
        assertThat(ex.getMessage()).isEqualTo(msg);
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
