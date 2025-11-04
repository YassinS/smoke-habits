package com.sassi.smokehabits.dto.response;

public class SmokeContextResponse {
    private String context;
    private String colorUI;

    public SmokeContextResponse(String context, String colorUI) {
        this.context = context;
        this.colorUI = colorUI;
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
