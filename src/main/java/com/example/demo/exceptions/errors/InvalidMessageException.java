package com.example.demo.exceptions.errors;

public class InvalidMessageException extends RuntimeException {
    public InvalidMessageException(String message) {
        super(message);
    }

    public InvalidMessageException() {
    }
}
