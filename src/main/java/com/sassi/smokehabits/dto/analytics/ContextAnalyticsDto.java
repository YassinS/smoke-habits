package com.sassi.smokehabits.dto.analytics;

public class ContextAnalyticsDto {
    private String context;
    private long cigaretteCount;
    private Double avgCraving;

    public ContextAnalyticsDto(String context, long cigaretteCount, Double avgCraving) {
        this.context = context;
        this.cigaretteCount = cigaretteCount;
        this.avgCraving = avgCraving;
    }

    public String getContext() {
        return context;
    }

    public long getCigaretteCount() {
        return cigaretteCount;
    }

    public Double getAvgCraving() {
        return avgCraving;
    }
}
