package com.sassi.smokehabits.dto.response;

import java.util.UUID;

public class SmokeContextResponse {
    private UUID id;
    private String context;
    private String colorUI;

    public SmokeContextResponse(UUID id, String context, String colorUI) {
        this.id = id;
        this.context = context;
        this.colorUI = colorUI;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getColorUI() {
        return colorUI;
    }

    public void setColorUI(String colorUI) {
        this.colorUI = colorUI;
    }
}
