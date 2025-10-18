
/*package com.example.orderservice.utils.exceptions;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class DuplicateVinExceptionTest {

    @Test
    void constructor_messageOnly_setsMessageAndNoCause() {
        DuplicateVinException ex = new DuplicateVinException("dup vin");
        assertThat(ex.getMessage()).isEqualTo("dup vin");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void constructor_messageAndCause_setsBoth() {
        Exception cause = new Exception("dup!");
        DuplicateVinException ex = new DuplicateVinException("dup vin", cause);
        assertThat(ex.getMessage()).isEqualTo("dup vin");
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
*/
package com.example.orderservice.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DuplicateVinExceptionTest {

    @Test
    void defaultConstructor_setsNoMessageAndNoCause() {
        DuplicateVinException ex = new DuplicateVinException();
        assertThat(ex.getMessage()).isNull();
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void causeConstructor_setsCauseAndUsesCauseToStringAsMessage() {
        Throwable cause = new IllegalStateException("duplicate vin");
        DuplicateVinException ex = new DuplicateVinException(cause);
        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex.getMessage()).isEqualTo(cause.toString());
    }

    @Test
    void messageConstructor_setsMessageAndNoCause() {
        String msg = "VIN already exists";
        DuplicateVinException ex = new DuplicateVinException(msg);
        assertThat(ex.getMessage()).isEqualTo(msg);
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void messageAndCauseConstructor_setsBoth() {
        String msg = "duplicate VIN error";
        Throwable cause = new RuntimeException("root cause");
        DuplicateVinException ex = new DuplicateVinException(msg, cause);
        assertThat(ex.getMessage()).isEqualTo(msg);
        assertThat(ex.getCause()).isSameAs(cause);
    }
}

