package com.sassi.smokehabits.dto.analytics;

import java.io.Serializable;

public class ContextAnalyticsDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String context;
    private long cigaretteCount;
    private Double avgCraving;

    public ContextAnalyticsDto(
        String context,
        long cigaretteCount,
        Double avgCraving
    ) {
        this.context = context;
        this.cigaretteCount = cigaretteCount;
        this.avgCraving = avgCraving;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public long getCigaretteCount() {
        return cigaretteCount;
    }

    public void setCigaretteCount(long cigaretteCount) {
        this.cigaretteCount = cigaretteCount;
    }

    public Double getAvgCraving() {
        return avgCraving;
    }

    public void setAvgCraving(Double avgCraving) {
        this.avgCraving = avgCraving;
    }
}
