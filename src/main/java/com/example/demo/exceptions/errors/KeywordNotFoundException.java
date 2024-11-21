package com.example.demo.exceptions.errors;

public class KeywordNotFoundException extends RuntimeException {
    public KeywordNotFoundException(String message) {
        super(message);
    }

    public KeywordNotFoundException() {
    }
}
