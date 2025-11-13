package com.sassi.smokehabits.dto.kafka;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class SmokeContextUpdatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID contextId;
    private UUID userId;
    private String oldContextLabel;
    private String newContextLabel;
    private String oldColorUI;
    private String newColorUI;
    private Instant updatedAt;
    private Instant eventTime;

    public SmokeContextUpdatedEvent() {
        this.eventTime = Instant.now();
    }

    public SmokeContextUpdatedEvent(
        UUID contextId,
        UUID userId,
        String oldContextLabel,
        String newContextLabel,
        String oldColorUI,
        String newColorUI,
        Instant updatedAt
    ) {
        this.contextId = contextId;
        this.userId = userId;
        this.oldContextLabel = oldContextLabel;
        this.newContextLabel = newContextLabel;
        this.oldColorUI = oldColorUI;
        this.newColorUI = newColorUI;
        this.updatedAt = updatedAt;
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

    public String getOldContextLabel() {
        return oldContextLabel;
    }

    public void setOldContextLabel(String oldContextLabel) {
        this.oldContextLabel = oldContextLabel;
    }

    public String getNewContextLabel() {
        return newContextLabel;
    }

    public void setNewContextLabel(String newContextLabel) {
        this.newContextLabel = newContextLabel;
    }

    public String getOldColorUI() {
        return oldColorUI;
    }

    public void setOldColorUI(String oldColorUI) {
        this.oldColorUI = oldColorUI;
    }

    public String getNewColorUI() {
        return newColorUI;
    }

    public void setNewColorUI(String newColorUI) {
        this.newColorUI = newColorUI;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
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
            "SmokeContextUpdatedEvent{" +
            "contextId=" +
            contextId +
            ", userId=" +
            userId +
            ", oldContextLabel='" +
            oldContextLabel +
            '\'' +
            ", newContextLabel='" +
            newContextLabel +
            '\'' +
            ", oldColorUI='" +
            oldColorUI +
            '\'' +
            ", newColorUI='" +
            newColorUI +
            '\'' +
            ", updatedAt=" +
            updatedAt +
            ", eventTime=" +
            eventTime +
            '}'
        );
    }
}
