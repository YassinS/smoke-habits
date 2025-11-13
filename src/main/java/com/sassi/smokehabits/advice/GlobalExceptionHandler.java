package com.sassi.smokehabits.advice;

import com.sassi.smokehabits.dto.response.ValidationErrorResponse;
import com.sassi.smokehabits.exception.AuthenticationError;
import com.sassi.smokehabits.exception.ValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(
        GlobalExceptionHandler.class
    );

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
        ValidationException ex
    ) {
        log.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ValidationErrorResponse(ex.getMessage())
        );
    }

    @ExceptionHandler(AuthenticationError.class)
    public ResponseEntity<ValidationErrorResponse> handleAuthenticationError(
        AuthenticationError ex
    ) {
        log.warn("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new ValidationErrorResponse(ex.getMessage())
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ValidationErrorResponse> handleExpiredJwtException(
        ExpiredJwtException ex
    ) {
        log.warn("JWT token has expired: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new ValidationErrorResponse("JWT token has expired")
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationErrorResponse> handleGenericException(
        Exception ex
    ) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new ValidationErrorResponse("An unexpected error occurred")
        );
    }
}
