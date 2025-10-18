package com.example.orderservice.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ExceptionClassesTest {

    //──────────────────────────────────────────────────────────────────────────────
    // InvalidInputException
    //──────────────────────────────────────────────────────────────────────────────

    @Test
    void invalidInputException_messageOnly() {
        InvalidInputException ex = new InvalidInputException("bad input");
        assertThat(ex).hasMessage("bad input");
        assertThat(ex).hasNoCause();
    }

    @Test
    void invalidInputException_messageAndCause() {
        RuntimeException cause = new RuntimeException("root");
        InvalidInputException ex = new InvalidInputException("bad input", cause);
        assertThat(ex).hasMessage("bad input");
        assertThat(ex).hasCause(cause);
    }

    //──────────────────────────────────────────────────────────────────────────────
    // NotFoundException
    //──────────────────────────────────────────────────────────────────────────────

    @Test
    void notFoundException_messageOnly() {
        NotFoundException ex = new NotFoundException("not found");
        assertThat(ex).hasMessage("not found");
        assertThat(ex).hasNoCause();
    }

    @Test
    void notFoundException_messageAndCause() {
        IllegalArgumentException cause = new IllegalArgumentException("arg bad");
        NotFoundException ex = new NotFoundException("not found", cause);
        assertThat(ex).hasMessage("not found");
        assertThat(ex).hasCause(cause);
    }

    //──────────────────────────────────────────────────────────────────────────────
    // DuplicateVinException
    //──────────────────────────────────────────────────────────────────────────────

    @Test
    void duplicateVinException_messageOnly() {
        DuplicateVinException ex = new DuplicateVinException("dup vin");
        assertThat(ex).hasMessage("dup vin");
        assertThat(ex).hasNoCause();
    }

    @Test
    void duplicateVinException_messageAndCause() {
        Exception cause = new Exception("duplicate!");
        DuplicateVinException ex = new DuplicateVinException("dup vin", cause);
        assertThat(ex).hasMessage("dup vin");
        assertThat(ex).hasCause(cause);
    }
}
