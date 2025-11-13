package com.sassi.smokehabits.dto.kafka;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class SmokeContextCreatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID contextId;
    private UUID userId;
    private String contextLabel;
    private String colorUI;
    private Instant createdAt;
    private Instant eventTime;

    public SmokeContextCreatedEvent() {
        this.eventTime = Instant.now();
    }

    public SmokeContextCreatedEvent(
        UUID contextId,
        UUID userId,
        String contextLabel,
        String colorUI,
        Instant createdAt
    ) {
        this.contextId = contextId;
        this.userId = userId;
        this.contextLabel = contextLabel;
        this.colorUI = colorUI;
        this.createdAt = createdAt;
        this.eventTime = Instant.now();
    }

    public UUID getContextId() {
        return contextId;
    }

    public void setContextId(UUID contextId) {
        this.contextId = contextId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getContextLabel() {
        return contextLabel;
    }

    public void setContextLabel(String contextLabel) {
        this.contextLabel = contextLabel;
    }

    public String getColorUI() {
        return colorUI;
    }

    public void setColorUI(String colorUI) {
        this.colorUI = colorUI;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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
            "SmokeContextCreatedEvent{" +
            "contextId=" +
            contextId +
            ", userId=" +
            userId +
            ", contextLabel='" +
            contextLabel +
            '\'' +
            ", colorUI='" +
            colorUI +
            '\'' +
            ", createdAt=" +
            createdAt +
            ", eventTime=" +
            eventTime +
            '}'
        );
    }
}
