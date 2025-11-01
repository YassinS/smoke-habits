package com.sassi.smokehabits.dto.analytics;
import java.time.LocalDate;

public class DailyCigaretteStats {

    private LocalDate day;
    private int count;
    private double avgCraving;

    private double movingAvgCount;
    private double movingAvgCraving;

    private double trendCount;
    private double trendCraving;

    public DailyCigaretteStats(LocalDate day, int count, double avgCraving) {
        this.day = day;
        this.count = count;
        this.avgCraving = avgCraving;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAvgCraving() {
        return avgCraving;
    }

    public void setAvgCraving(double avgCraving) {
        this.avgCraving = avgCraving;
    }

    public double getMovingAvgCount() {
        return movingAvgCount;
    }

    public void setMovingAvgCount(double movingAvgCount) {
        this.movingAvgCount = movingAvgCount;
    }

    public double getMovingAvgCraving() {
        return movingAvgCraving;
    }

    public void setMovingAvgCraving(double movingAvgCraving) {
        this.movingAvgCraving = movingAvgCraving;
    }

    public double getTrendCount() {
        return trendCount;
    }

    public void setTrendCount(double trendCount) {
        this.trendCount = trendCount;
    }

    public double getTrendCraving() {
        return trendCraving;
    }

    public void setTrendCraving(double trendCraving) {
        this.trendCraving = trendCraving;
    }
}

