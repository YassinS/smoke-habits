package com.sassi.smokehabits.dto.request;

import java.util.UUID;

public class SmokeContextDeletionRequest {
    private UUID smokeContextId;

    public SmokeContextDeletionRequest(UUID smokeContextId) {
        this.smokeContextId = smokeContextId;
    }

    public UUID getSmokeContextId() {
        return smokeContextId;
    }

    public void setSmokeContextId(UUID smokeContextId) {
        this.smokeContextId = smokeContextId;
    }
}
