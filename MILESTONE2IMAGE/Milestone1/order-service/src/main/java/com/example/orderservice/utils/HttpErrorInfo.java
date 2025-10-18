package com.example.orderservice.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
public class HttpErrorInfo {

    private final ZonedDateTime timestamp;
    private final String path;
    private final HttpStatus httpStatus;
    private final String message;

    // Constructor for Jackson to use during deserialization from JSON
    @JsonCreator
    public HttpErrorInfo(
            @JsonProperty("timestamp") ZonedDateTime timestamp,
            @JsonProperty("httpStatus") HttpStatus httpStatus,
            @JsonProperty("path") String path,
            @JsonProperty("message") String message) {
        this.timestamp = timestamp;
        this.httpStatus = httpStatus;
        this.path = path;
        this.message = message;
    }

    // Original constructor for creating new errors (can be kept)
    public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
        this.timestamp = ZonedDateTime.now();
        this.httpStatus = httpStatus;
        this.path = path;
        this.message = message;
    }
}