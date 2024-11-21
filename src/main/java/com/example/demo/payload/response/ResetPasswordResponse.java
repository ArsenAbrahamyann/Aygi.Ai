package com.example.demo.payload.response;

import org.springframework.http.HttpStatus;

public class ResetPasswordResponse {
    private String message;
    private HttpStatus status;

    public ResetPasswordResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}