package com.sassi.smokehabits.service;

import com.sassi.smokehabits.dto.analytics.*;
import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.repository.CigaretteEntryRepository;
import com.sassi.smokehabits.repository.UserRepository;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final CigaretteEntryRepository cigaretteEntryRepository;
    private final UserRepository userRepository;

    private static final int MOVING_AVG_WINDOW = 7;
    private static final int ROLLING_CRAVING_7 = 7;
    private static final int ROLLING_CRAVING_14 = 14;

    public AnalyticsService(
        CigaretteEntryRepository cigaretteEntryRepository,
        UserRepository userRepository
    ) {
        this.cigaretteEntryRepository = cigaretteEntryRepository;
        this.userRepository = userRepository;
    }

    // ----------- PUBLIC METHODS -----------

    @Cacheable(value = "analytics:daily", key = "#userId")
    public List<DailyCigaretteStats> getDailyStats(UUID userId) {
        List<CigaretteEntry> entries = getUserEntries(userId);

        Map<LocalDate, List<CigaretteEntry>> grouped = entries
            .stream()
            .collect(
                Collectors.groupingBy(e -> {
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(
                        e.getTimestamp(),
                        ZoneOffset.UTC
                    );
                    return localDateTime.toLocalDate();
                })
            );

        List<DailyCigaretteStats> dailyStats = grouped
            .entrySet()
            .stream()
            .map(e -> {
                List<CigaretteEntry> list = e.getValue();
                double avgCraving = list
                    .stream()
                    .mapToInt(CigaretteEntry::getCravingLevel)
                    .average()
                    .orElse(0);
                Instant dayInstant = e
                    .getKey()
                    .atStartOfDay()
                    .toInstant(ZoneOffset.UTC);
                return new DailyCigaretteStats(
                    dayInstant,
                    list.size(),
                    avgCraving
                );
            })
            .sorted(Comparator.comparing(DailyCigaretteStats::getDay))
            .toList();

        computeDailyTrendsAndMovingAverages(dailyStats);
        computeCravingRollingAverages(dailyStats);

        return dailyStats;
    }

    @Cacheable(value = "analytics:weekly", key = "#userId")
    public List<WeeklyCigaretteStats> getWeeklyStats(UUID userId) {
        List<CigaretteEntry> entries = getUserEntries(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-ww");

        Map<String, List<CigaretteEntry>> weeklyMap = entries
            .stream()
            .collect(
                Collectors.groupingBy(entry -> {
                    Instant instantTime = entry.getTimestamp();
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(
                        instantTime,
                        ZoneId.systemDefault()
                    );
                    return formatter.format(localDateTime);
                })
            );

        List<WeeklyCigaretteStats> stats = weeklyMap
            .entrySet()
            .stream()
            .map(e -> {
                List<CigaretteEntry> weekEntries = e.getValue();
                int totalCigarettes = weekEntries.size();
                double avgCraving = weekEntries
                    .stream()
                    .mapToInt(CigaretteEntry::getCravingLevel)
                    .average()
                    .orElse(0);
                return new WeeklyCigaretteStats(
                    e.getKey(),
                    totalCigarettes,
                    avgCraving
                );
            })
            .sorted(
                Comparator.comparing(ws -> {
                    String[] parts = ws.getWeekLabel().split("-");
                    int year = Integer.parseInt(parts[0]);
                    int week = Integer.parseInt(parts[1]);
                    return year * 100 + week;
                })
            )
            .toList();

        computeWeeklyTrends(stats);

        return stats;
    }

    @Cacheable(value = "analytics:monthly", key = "#userId")
    public List<MonthlyCigaretteStats> getMonthlyStats(UUID userId) {
        List<CigaretteEntry> entries = getUserEntries(userId);

        Map<YearMonth, List<CigaretteEntry>> grouped = entries
            .stream()
            .collect(
                Collectors.groupingBy(e -> {
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(
                        e.getTimestamp(),
                        ZoneId.systemDefault()
                    );
                    return YearMonth.from(localDateTime);
                })
            );

        List<MonthlyCigaretteStats> stats = grouped
            .entrySet()
            .stream()
            .map(entry -> {
                YearMonth month = entry.getKey();
                int total = entry.getValue().size();
                double avgCraving = entry
                    .getValue()
                    .stream()
                    .mapToInt(CigaretteEntry::getCravingLevel)
                    .average()
                    .orElse(0.0);
                return new MonthlyCigaretteStats(month, total, avgCraving);
            })
            .sorted(Comparator.comparing(MonthlyCigaretteStats::getMonth))
            .toList();

        computeMonthlyTrends(stats);

        return stats;
    }

    @Cacheable(value = "analytics:context", key = "#userId")
    public List<ContextAnalyticsDto> getContextAnalytics(UUID userId) {
        return cigaretteEntryRepository.findContextAnalyticsByUserId(userId);
    }

    @Cacheable(value = "analytics:longestStreak", key = "#userId")
    public int calculateLongestStreak(UUID userId) {
        List<Instant> dates = getUserEntries(userId)
            .stream()
            .map(CigaretteEntry::getTimestamp)
            .distinct()
            .sorted()
            .toList();

        if (dates.isEmpty()) return 0;

        int longest = 1,
            current = 1;

        for (int i = 1; i < dates.size(); i++) {
            if (
                dates.get(i).minusSeconds(24 * 60 * 60).equals(dates.get(i - 1))
            ) {
                current++;
                longest = Math.max(longest, current);
            } else {
                current = 1;
            }
        }
        return longest;
    }

    @Cacheable(value = "analytics:avgCraving", key = "#userId")
    public double getOverallAvgCraving(UUID userId) {
        return getUserEntries(userId)
            .stream()
            .mapToInt(CigaretteEntry::getCravingLevel)
            .average()
            .orElse(0.0);
    }

    private List<CigaretteEntry> getUserEntries(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return cigaretteEntryRepository.findAllByUserOrderByTimestampDesc(user);
    }

    private void computeDailyTrendsAndMovingAverages(
        List<DailyCigaretteStats> stats
    ) {
        for (int i = 0; i < stats.size(); i++) {
            int start = Math.max(0, i - MOVING_AVG_WINDOW + 1);
            List<DailyCigaretteStats> window = stats.subList(start, i + 1);

            double movingAvgCount = window
                .stream()
                .mapToInt(DailyCigaretteStats::getCount)
                .average()
                .orElse(0);
            double movingAvgCraving = window
                .stream()
                .mapToDouble(DailyCigaretteStats::getAvgCraving)
                .average()
                .orElse(0);

            DailyCigaretteStats current = stats.get(i);
            current.setMovingAvgCount(movingAvgCount);
            current.setMovingAvgCraving(movingAvgCraving);

            if (i > 0) {
                DailyCigaretteStats prev = stats.get(i - 1);
                current.setTrendCount(
                    calculateTrend(current.getCount(), prev.getCount())
                );
                current.setTrendCraving(
                    calculateTrend(
                        current.getAvgCraving(),
                        prev.getAvgCraving()
                    )
                );
            } else {
                current.setTrendCount(Trend.NEUTRAL);
                current.setTrendCraving(Trend.NEUTRAL);
            }
        }
    }

    private void computeCravingRollingAverages(
        List<DailyCigaretteStats> stats
    ) {
        for (int i = 0; i < stats.size(); i++) {
            stats
                .get(i)
                .setRollingAvgCraving7(
                    stats
                        .subList(Math.max(0, i - ROLLING_CRAVING_7 + 1), i + 1)
                        .stream()
                        .mapToDouble(DailyCigaretteStats::getAvgCraving)
                        .average()
                        .orElse(0.0)
                );
            stats
                .get(i)
                .setRollingAvgCraving14(
                    stats
                        .subList(Math.max(0, i - ROLLING_CRAVING_14 + 1), i + 1)
                        .stream()
                        .mapToDouble(DailyCigaretteStats::getAvgCraving)
                        .average()
                        .orElse(0.0)
                );
        }
    }

    private void computeWeeklyTrends(List<WeeklyCigaretteStats> stats) {
        for (int i = 0; i < stats.size(); i++) {
            if (i == 0) {
                stats.get(i).setTrendCount(Trend.NEUTRAL);
                stats.get(i).setTrendCraving(Trend.NEUTRAL);
            } else {
                WeeklyCigaretteStats prev = stats.get(i - 1);
                WeeklyCigaretteStats current = stats.get(i);
                current.setTrendCount(
                    calculateTrend(
                        current.getTotalCigarettes(),
                        prev.getTotalCigarettes()
                    )
                );
                current.setTrendCraving(
                    calculateTrend(
                        current.getAvgCraving(),
                        prev.getAvgCraving()
                    )
                );
            }
        }
    }

    private void computeMonthlyTrends(List<MonthlyCigaretteStats> stats) {
        for (int i = 0; i < stats.size(); i++) {
            if (i == 0) {
                stats.get(i).setTrendCount(Trend.NEUTRAL);
                stats.get(i).setTrendCraving(Trend.NEUTRAL);
            } else {
                MonthlyCigaretteStats prev = stats.get(i - 1);
                MonthlyCigaretteStats current = stats.get(i);
                current.setTrendCount(
                    calculateTrend(
                        current.getTotalCigarettes(),
                        prev.getTotalCigarettes()
                    )
                );
                current.setTrendCraving(
                    calculateTrend(
                        current.getAvgCraving(),
                        prev.getAvgCraving()
                    )
                );
            }
        }
    }

    private Trend calculateTrend(double current, double previous) {
        if (current > previous) return Trend.UP;
        if (current < previous) return Trend.DOWN;
        return Trend.NEUTRAL;
    }
}
