package com.sassi.smokehabits.dto.kafka;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class UserRegisteredEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID userId;
    private String email;
    private Instant registeredAt;
    private Instant eventTime;

    public UserRegisteredEvent() {
        this.eventTime = Instant.now();
    }

    public UserRegisteredEvent(UUID userId, String email, Instant registeredAt) {
        this.userId = userId;
        this.email = email;
        this.registeredAt = registeredAt;
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

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
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
            "UserRegisteredEvent{" +
            "userId=" +
            userId +
            ", email='" +
            email +
            '\'' +
            ", registeredAt=" +
            registeredAt +
            ", eventTime=" +
            eventTime +
            '}'
        );
    }
}
