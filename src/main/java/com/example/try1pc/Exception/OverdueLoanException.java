package com.example.try1pc.Exception;

public class OverdueLoanException extends RuntimeException {
    public OverdueLoanException(String message) {
        super(message);
    }
}
