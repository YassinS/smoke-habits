package com.sassi.smokehabits.dto.kafka;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class CigaretteLoggedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long cigaretteId;
    private UUID userId;
    private Instant timestamp;
    private int cravingLevel;
    private UUID smokeContextId;
    private String smokeContextLabel;
    private Instant eventTime;

    public CigaretteLoggedEvent() {
        this.eventTime = Instant.now();
    }

    public CigaretteLoggedEvent(
        Long cigaretteId,
        UUID userId,
        Instant timestamp,
        int cravingLevel,
        UUID smokeContextId,
        String smokeContextLabel
    ) {
        this.cigaretteId = cigaretteId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.cravingLevel = cravingLevel;
        this.smokeContextId = smokeContextId;
        this.smokeContextLabel = smokeContextLabel;
        this.eventTime = Instant.now();
    }

    public Long getCigaretteId() {
        return cigaretteId;
    }

    public void setCigaretteId(Long cigaretteId) {
        this.cigaretteId = cigaretteId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getCravingLevel() {
        return cravingLevel;
    }

    public void setCravingLevel(int cravingLevel) {
        this.cravingLevel = cravingLevel;
    }

    public UUID getSmokeContextId() {
        return smokeContextId;
    }

    public void setSmokeContextId(UUID smokeContextId) {
        this.smokeContextId = smokeContextId;
    }

    public String getSmokeContextLabel() {
        return smokeContextLabel;
    }

    public void setSmokeContextLabel(String smokeContextLabel) {
        this.smokeContextLabel = smokeContextLabel;
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
            "CigaretteLoggedEvent{" +
            "cigaretteId=" +
            cigaretteId +
            ", userId=" +
            userId +
            ", timestamp=" +
            timestamp +
            ", cravingLevel=" +
            cravingLevel +
            ", smokeContextId=" +
            smokeContextId +
            ", smokeContextLabel='" +
            smokeContextLabel +
            '\'' +
            ", eventTime=" +
            eventTime +
            '}'
        );
    }
}
