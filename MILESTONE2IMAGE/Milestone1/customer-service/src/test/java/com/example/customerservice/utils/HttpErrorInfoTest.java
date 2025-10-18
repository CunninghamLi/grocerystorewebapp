package com.example.customerservice.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class HttpErrorInfoTest {

    @Test
    void constructor_shouldPopulateAllFields() {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String path = "/some/path";
        String message = "oops";

        HttpErrorInfo info = new HttpErrorInfo(status, path, message);

        assertNotNull(info.getTimestamp());
        assertEquals(status, info.getHttpStatus());
        assertEquals(path, info.getPath());
        assertEquals(message, info.getMessage());
    }
}
