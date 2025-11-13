package com.sassi.smokehabits.dto.kafka;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class SmokeContextDeletedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID contextId;
    private UUID userId;
    private String contextLabel;
    private String colorUI;
    private Instant deletedAt;
    private Instant eventTime;

    public SmokeContextDeletedEvent() {
        this.eventTime = Instant.now();
    }

    public SmokeContextDeletedEvent(
        UUID contextId,
        UUID userId,
        String contextLabel,
        String colorUI,
        Instant deletedAt
    ) {
        this.contextId = contextId;
        this.userId = userId;
        this.contextLabel = contextLabel;
        this.colorUI = colorUI;
        this.deletedAt = deletedAt;
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
            "SmokeContextDeletedEvent{" +
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
            ", deletedAt=" +
            deletedAt +
            ", eventTime=" +
            eventTime +
            '}'
        );
    }
}
