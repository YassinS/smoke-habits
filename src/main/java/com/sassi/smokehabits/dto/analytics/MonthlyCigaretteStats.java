package com.sassi.smokehabits.dto.analytics;

import java.time.YearMonth;

public class MonthlyCigaretteStats {
    private YearMonth month;
    private int totalCigarettes;
    private double avgCravingLevel;

    public MonthlyCigaretteStats(YearMonth month, int totalCigarettes, double avgCravingLevel) {
        this.month = month;
        this.totalCigarettes = totalCigarettes;
        this.avgCravingLevel = avgCravingLevel;
    }

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

    public double getAvgCravingLevel() {
        return avgCravingLevel;
    }

    public void setAvgCravingLevel(double avgCravingLevel) {
        this.avgCravingLevel = avgCravingLevel;
    }
}

