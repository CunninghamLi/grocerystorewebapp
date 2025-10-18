/*package com.example.customerservice.utils;

import com.example.customerservice.utils.exceptions.DuplicateVinException;
import com.example.customerservice.utils.exceptions.InvalidInputException;
import com.example.customerservice.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalControllerExceptionHandlerTest {

    private WebTestClient client;

    @BeforeEach
    void setup() {
        // A tiny controller that just throws each exception:
        @org.springframework.web.bind.annotation.RestController
        class TestController {
            @org.springframework.web.bind.annotation.GetMapping("/notfound")
            void notFound() { throw new NotFoundException("no such resource"); }

            @org.springframework.web.bind.annotation.GetMapping("/invalid")
            void invalid() { throw new InvalidInputException("bad data"); }

            @org.springframework.web.bind.annotation.GetMapping("/duplicate")
            void duplicate() { throw new DuplicateVinException("dup"); }
        }

        // Bind only our TEST-ONLY advice (not the prod one):
        this.client = WebTestClient
                .bindToController(new TestController())
                .controllerAdvice(new TestGlobalExceptionHandler())
                .build();
    }
/*
    @Test
    void notFoundEndpoint_shouldReturn404AndErrorInfo() {
        client.get().uri("/notfound")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("no such resource")
                .jsonPath("$.httpStatus").isEqualTo("NOT_FOUND")
                .jsonPath("$.path").value(path ->
                        assertTrue(path.toString().contains("/notfound"))
                );
    }

    @Test
    void invalidEndpoint_shouldReturn422AndErrorInfo() {
        client.get().uri("/invalid")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .expectBody()
                .jsonPath("$.message").isEqualTo("bad data")
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                .jsonPath("$.path").value(path ->
                        assertTrue(path.toString().contains("/invalid"))
                );
    }

    @Test
    void duplicateEndpoint_shouldReturn422AndErrorInfo() {
        client.get().uri("/duplicate")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .expectBody()
                .jsonPath("$.message").isEqualTo("dup")
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                .jsonPath("$.path").value(path ->
                        assertTrue(path.toString().contains("/duplicate"))
                );
    }

   ---
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @RestControllerAdvice
    public static class TestGlobalExceptionHandler {

        @ExceptionHandler(NotFoundException.class)
        public org.springframework.http.ResponseEntity<HttpErrorInfo> handleNotFound(
                NotFoundException ex, WebRequest request) {

            HttpErrorInfo info = createInfo(HttpStatus.NOT_FOUND, request, ex);
            return new org.springframework.http.ResponseEntity<>(info, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler({ InvalidInputException.class, DuplicateVinException.class })
        public org.springframework.http.ResponseEntity<HttpErrorInfo> handleUnprocessable(
                RuntimeException ex, WebRequest request) {

            HttpErrorInfo info = createInfo(HttpStatus.UNPROCESSABLE_ENTITY, request, ex);
            return new org.springframework.http.ResponseEntity<>(info, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        private static HttpErrorInfo createInfo(
                HttpStatus status, WebRequest req, Exception ex) {

            String path = req.getDescription(false).replace("uri=", "");
            return new HttpErrorInfo(status, path, ex.getMessage());
        }
    }
}
*/
// File: customer-service/src/test/java/com/example/customerservice/utils/GlobalControllerExceptionHandlerTest.java

package com.example.customerservice.utils;

import com.example.customerservice.utils.exceptions.DuplicateVinException;
import com.example.customerservice.utils.exceptions.InvalidInputException;
import com.example.customerservice.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

class GlobalControllerExceptionHandlerTest {

    private GlobalControllerExceptionHandler handler;
    private WebRequest request;
    private static final String EXPECTED_PATH = "uri=/test/path";

    @BeforeEach
    void setUp() {
        handler = new GlobalControllerExceptionHandler();
        MockHttpServletRequest servletReq = new MockHttpServletRequest();
        servletReq.setRequestURI("/test/path");
        request = new ServletWebRequest(servletReq);
    }

    @Test
    void handleNotFoundException_shouldBuildNotFoundInfo() {
        NotFoundException ex = new NotFoundException("missing!");
        HttpErrorInfo info = handler.handleNotFoundException(request, ex);

        assertThat(info.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(info.getMessage()).isEqualTo("missing!");
        assertThat(info.getPath()).isEqualTo(EXPECTED_PATH);
        assertThat(info.getTimestamp()).isNotNull();
    }

    @Test
    void handleInvalidInputException_shouldBuildBadRequestInfo() {
        InvalidInputException ex = new InvalidInputException("bad input");
        HttpErrorInfo info = handler.handleInvalidInputException(request, ex);

        assertThat(info.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(info.getMessage()).isEqualTo("bad input");
        assertThat(info.getPath()).isEqualTo(EXPECTED_PATH);
        assertThat(info.getTimestamp()).isNotNull();
    }

    @Test
    void handleDuplicateVinException_shouldBuildConflictInfo() {
        DuplicateVinException ex = new DuplicateVinException("dup VIN");
        HttpErrorInfo info = handler.handleDuplicateVinException(request, ex);

        assertThat(info.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(info.getMessage()).isEqualTo("dup VIN");
        assertThat(info.getPath()).isEqualTo(EXPECTED_PATH);
        assertThat(info.getTimestamp()).isNotNull();
    }

    @Test
    void createHttpErrorInfo_privateMethod_shouldPopulateAllFields() throws Exception {
        Method m = GlobalControllerExceptionHandler.class
                .getDeclaredMethod("createHttpErrorInfo", HttpStatus.class, WebRequest.class, Exception.class);
        m.setAccessible(true);

        Exception ex = new Exception("oops");
        HttpErrorInfo info = (HttpErrorInfo) m.invoke(handler, HttpStatus.I_AM_A_TEAPOT, request, ex);

        assertThat(info.getHttpStatus()).isEqualTo(HttpStatus.I_AM_A_TEAPOT);
        assertThat(info.getMessage()).isEqualTo("oops");
        assertThat(info.getPath()).isEqualTo(EXPECTED_PATH);
        assertThat(info.getTimestamp()).isNotNull();
    }
}
