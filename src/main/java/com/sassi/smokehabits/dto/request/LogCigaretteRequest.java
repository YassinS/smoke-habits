package com.sassi.smokehabits.dto.request;

import com.sassi.smokehabits.entity.SmokeContext;

import java.util.UUID;

public class LogCigaretteRequest {
    private Integer cravingLevel;
    private UUID smokeContext = null;

    public LogCigaretteRequest(Integer cravingLevel, UUID smokeContext) {
        this.cravingLevel = cravingLevel;
        this.smokeContext = smokeContext;
    }

    public Integer getCravingLevel() { return cravingLevel; }
    public void setCravingLevel(Integer cravingLevel) { this.cravingLevel = cravingLevel; }

    public UUID getSmokeContext() {
        return smokeContext;
    }

    public void setSmokeContext(UUID smokeContext) {
        this.smokeContext = smokeContext;
    }
}
