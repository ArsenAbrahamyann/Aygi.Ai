package com.example.demo.exceptions.errors;

public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException(String message) {
        super(message);
    }

    public InvalidFileTypeException() {
    }
}
