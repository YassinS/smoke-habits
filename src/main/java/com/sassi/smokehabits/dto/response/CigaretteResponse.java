package com.sassi.smokehabits.dto.response;

import java.io.Serializable;
import java.time.Instant;

public class CigaretteResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private Instant timestamp;
    private int cravingLevel;
    private SmokeContextResponse contextResponse;

    public CigaretteResponse(
        long id,
        Instant timestamp,
        int cravingLevel,
        SmokeContextResponse contextResponse
    ) {
        this.id = id;
        this.timestamp = timestamp;
        this.cravingLevel = cravingLevel;
        this.contextResponse = contextResponse;
    }

    public CigaretteResponse(long id, Instant timestamp, int cravingLevel) {
        this.id = id;
        this.timestamp = timestamp;
        this.cravingLevel = cravingLevel;
    }

    public CigaretteResponse() {}

    public long getId() {
        return id;
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

    public SmokeContextResponse getContextResponse() {
        return contextResponse;
    }

    public void setContextResponse(SmokeContextResponse contextResponse) {
        this.contextResponse = contextResponse;
    }
}
