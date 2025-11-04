package com.sassi.smokehabits.dto.response;

import java.time.Instant;
import java.time.LocalDateTime;

public class CigaretteResponse {
    private long id;
    private Instant timestamp;

    public CigaretteResponse(long id, Instant timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public CigaretteResponse() {
    }

    public long getId() {
        return id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
