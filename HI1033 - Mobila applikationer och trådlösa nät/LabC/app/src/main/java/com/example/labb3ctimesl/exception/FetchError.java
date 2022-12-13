package com.example.labb3ctimesl.exception;

public class FetchError {
    private String message;

    public FetchError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
