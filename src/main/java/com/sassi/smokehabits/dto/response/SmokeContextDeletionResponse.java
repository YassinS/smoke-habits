package com.sassi.smokehabits.dto.response;

public class SmokeContextDeletionResponse {
    private boolean deleted;
    private String message;

    public SmokeContextDeletionResponse(boolean deleted) {
        this.deleted = deleted;
    }

    public SmokeContextDeletionResponse(boolean deleted, String message) {
        this.deleted = deleted;
        this.message = message;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
