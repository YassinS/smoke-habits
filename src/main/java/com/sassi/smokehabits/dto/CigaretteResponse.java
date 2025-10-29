package com.sassi.smokehabits.dto;

import java.time.LocalDateTime;

public class CigaretteResponse {
    private long id;
    private LocalDateTime timestamp;

    public CigaretteResponse(long id, LocalDateTime timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public CigaretteResponse() {
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
