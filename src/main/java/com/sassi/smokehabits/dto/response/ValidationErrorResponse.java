package com.sassi.smokehabits.dto.response;

import java.time.LocalDateTime;

public class ValidationErrorResponse {
    private String message;
    private String field;
    private LocalDateTime timestamp;

    public ValidationErrorResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ValidationErrorResponse(String message, String field) {
        this.message = message;
        this.field = field;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
