package com.sassi.smokehabits.dto.analytics;

import java.io.Serializable;

public class WeeklyCigaretteStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private String weekLabel; // e.g., "2025-09"
    private int totalCigarettes;
    private double avgCraving;

    // Trend indicators compared to previous week
    private Trend trendCount;
    private Trend trendCraving;

    // ----------------- Constructors -----------------
    public WeeklyCigaretteStats() {}

    public WeeklyCigaretteStats(
        String weekLabel,
        int totalCigarettes,
        double avgCraving
    ) {
        this.weekLabel = weekLabel;
        this.totalCigarettes = totalCigarettes;
        this.avgCraving = avgCraving;
    }

    // ----------------- Getters & Setters -----------------
    public String getWeekLabel() {
        return weekLabel;
    }

    public void setWeekLabel(String weekLabel) {
        this.weekLabel = weekLabel;
    }

    public int getTotalCigarettes() {
        return totalCigarettes;
    }

    public void setTotalCigarettes(int totalCigarettes) {
        this.totalCigarettes = totalCigarettes;
    }

    public double getAvgCraving() {
        return avgCraving;
    }

    public void setAvgCraving(double avgCraving) {
        this.avgCraving = avgCraving;
    }

    public Trend getTrendCount() {
        return trendCount;
    }

    public void setTrendCount(Trend trendCount) {
        this.trendCount = trendCount;
    }

    public Trend getTrendCraving() {
        return trendCraving;
    }

    public void setTrendCraving(Trend trendCraving) {
        this.trendCraving = trendCraving;
    }
}
