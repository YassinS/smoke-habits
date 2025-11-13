package com.sassi.smokehabits.dto.kafka;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class UserDeletedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID userId;
    private String email;
    private Instant deletedAt;
    private Instant eventTime;

    public UserDeletedEvent() {
        this.eventTime = Instant.now();
    }

    public UserDeletedEvent(UUID userId, String email, Instant deletedAt) {
        this.userId = userId;
        this.email = email;
        this.deletedAt = deletedAt;
        this.eventTime = Instant.now();
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return (
            "UserDeletedEvent{" +
            "userId=" +
            userId +
            ", email='" +
            email +
            '\'' +
            ", deletedAt=" +
            deletedAt +
            ", eventTime=" +
            eventTime +
            '}'
        );
    }
}
