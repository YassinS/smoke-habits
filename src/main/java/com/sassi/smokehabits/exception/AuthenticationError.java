package com.sassi.smokehabits.exception;

public class AuthenticationError extends RuntimeException {
    public AuthenticationError(String message) {
        super(message);
    }
}
