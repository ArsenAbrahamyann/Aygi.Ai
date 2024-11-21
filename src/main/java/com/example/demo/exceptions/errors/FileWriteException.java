package com.example.demo.exceptions.errors;

public class FileWriteException extends RuntimeException {
    public FileWriteException(String message) {
        super(message);
    }
}
