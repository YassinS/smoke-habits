package com.sassi.smokehabits.dto.analytics;

import java.time.LocalDate;

public class DailyCigaretteStats {

    private LocalDate day;
    private int count;
    private double avgCraving;

    // Moving averages (window: 7 days)
    private double movingAvgCount;
    private double movingAvgCraving;

    // Rolling averages for craving
    private double rollingAvgCraving7;
    private double rollingAvgCraving14;

    // Trend indicators
    private Trend trendCount;
    private Trend trendCraving;

    // ----------------- Constructors -----------------
    public DailyCigaretteStats() { }

    public DailyCigaretteStats(LocalDate day, int count, double avgCraving) {
        this.day = day;
        this.count = count;
        this.avgCraving = avgCraving;
    }

    // ----------------- Getters & Setters -----------------
    public LocalDate getDay() { return day; }
    public void setDay(LocalDate day) { this.day = day; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public double getAvgCraving() { return avgCraving; }
    public void setAvgCraving(double avgCraving) { this.avgCraving = avgCraving; }

    public double getMovingAvgCount() { return movingAvgCount; }
    public void setMovingAvgCount(double movingAvgCount) { this.movingAvgCount = movingAvgCount; }

    public double getMovingAvgCraving() { return movingAvgCraving; }
    public void setMovingAvgCraving(double movingAvgCraving) { this.movingAvgCraving = movingAvgCraving; }

    public double getRollingAvgCraving7() { return rollingAvgCraving7; }
    public void setRollingAvgCraving7(double rollingAvgCraving7) { this.rollingAvgCraving7 = rollingAvgCraving7; }

    public double getRollingAvgCraving14() { return rollingAvgCraving14; }
    public void setRollingAvgCraving14(double rollingAvgCraving14) { this.rollingAvgCraving14 = rollingAvgCraving14; }

    public Trend getTrendCount() { return trendCount; }
    public void setTrendCount(Trend trendCount) { this.trendCount = trendCount; }

    public Trend getTrendCraving() { return trendCraving; }
    public void setTrendCraving(Trend trendCraving) { this.trendCraving = trendCraving; }
}
