package com.sassi.smokehabits.dto.analytics;

import java.util.List;

public record TrendResponse(
        List<WeeklyCigaretteStats> weeklyStats,
        List<MonthlyCigaretteStats> monthlyStats,
        int longestStreak,
        double avgCravingOverall
) {}
