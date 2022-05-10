package com.backend;

import org.springframework.http.HttpStatus;

public class BackendException extends RuntimeException {
    private final HttpStatus statusCode;

    public BackendException(String reason, HttpStatus statusCode) {
        super(reason);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
