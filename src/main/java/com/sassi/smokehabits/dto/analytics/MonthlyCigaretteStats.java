package com.sassi.smokehabits.dto.analytics;

import java.io.Serializable;
import java.time.YearMonth;

public class MonthlyCigaretteStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private YearMonth month;
    private int totalCigarettes;
    private double avgCraving;

    // Trend indicators compared to previous month
    private Trend trendCount;
    private Trend trendCraving;

    // ----------------- Constructors -----------------
    public MonthlyCigaretteStats() {}

    public MonthlyCigaretteStats(
        YearMonth month,
        int totalCigarettes,
        double avgCraving
    ) {
        this.month = month;
        this.totalCigarettes = totalCigarettes;
        this.avgCraving = avgCraving;
    }

    // ----------------- Getters & Setters -----------------
    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
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
