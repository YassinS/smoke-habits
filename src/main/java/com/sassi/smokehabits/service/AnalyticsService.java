package com.sassi.smokehabits.service;

import com.sassi.smokehabits.dto.analytics.DailyCigaretteStats;
import com.sassi.smokehabits.dto.analytics.MonthlyCigaretteStats;
import com.sassi.smokehabits.dto.analytics.WeeklyCigaretteStats;
import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.repository.CigaretteEntryRepository;
import com.sassi.smokehabits.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private final CigaretteEntryRepository cigaretteEntryRepository;
    private final UserRepository userRepository;
    private final int MOVING_AVG_WINDOW = 7;

    public AnalyticsService(CigaretteEntryRepository cigaretteEntryRepository, UserRepository userRepository) {
        this.cigaretteEntryRepository = cigaretteEntryRepository;
        this.userRepository = userRepository;
    }

    public List<DailyCigaretteStats> getDailyStats(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<CigaretteEntry> entries = cigaretteEntryRepository.findAllByUserOrderByTimestampDesc(user);

        Map<LocalDate, List<CigaretteEntry>> grouped = entries.stream()
                .collect(Collectors.groupingBy(e -> e.getTimestamp().toLocalDate()));

        List<DailyCigaretteStats> dailyStats = grouped.entrySet().stream()
                .map(e -> {
                    List<CigaretteEntry> list = e.getValue();
                    double avgCraving = list.stream().mapToInt(CigaretteEntry::getCravingLevel).average().orElse(0);
                    return new DailyCigaretteStats(e.getKey(), list.size(), avgCraving);
                })
                .sorted(Comparator.comparing(DailyCigaretteStats::getDay))
                .toList();

        // compute moving averages and daily trends
        for (int i = 0; i < dailyStats.size(); i++) {
            int start = Math.max(0, i - MOVING_AVG_WINDOW + 1);
            List<DailyCigaretteStats> window = dailyStats.subList(start, i + 1);

            double movingAvgCount = window.stream().mapToInt(DailyCigaretteStats::getCount).average().orElse(0);
            double movingAvgCraving = window.stream().mapToDouble(DailyCigaretteStats::getAvgCraving).average().orElse(0);
            dailyStats.get(i).setMovingAvgCount(movingAvgCount);
            dailyStats.get(i).setMovingAvgCraving(movingAvgCraving);

            // simple daily trend: difference from previous day
            if (i > 0) {
                dailyStats.get(i).setTrendCount(dailyStats.get(i).getCount() - dailyStats.get(i - 1).getCount());
                dailyStats.get(i).setTrendCraving(dailyStats.get(i).getAvgCraving() - dailyStats.get(i - 1).getAvgCraving());
            } else {
                dailyStats.get(i).setTrendCount(0);
                dailyStats.get(i).setTrendCraving(0);
            }
        }

        return dailyStats;
    }

    public List<WeeklyCigaretteStats> getWeeklyStats(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<CigaretteEntry> entries = cigaretteEntryRepository.findAllByUserOrderByTimestampDesc(user);

        Map<String, List<CigaretteEntry>> weeklyMap = entries.stream()
                .collect(Collectors.groupingBy(entry -> {
                    LocalDate date = entry.getTimestamp().toLocalDate();
                    int week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                    int year = date.getYear();
                    return year + "-" + week; // "2025-44"
                }));

        return weeklyMap.entrySet().stream()
                .map(e -> {
                    List<CigaretteEntry> weekEntries = e.getValue();
                    int totalCigarettes = weekEntries.size();
                    double avgCraving = weekEntries.stream()
                            .mapToInt(CigaretteEntry::getCravingLevel)
                            .average()
                            .orElse(0);
                    return new WeeklyCigaretteStats(e.getKey(), totalCigarettes, avgCraving);
                })
                .sorted(Comparator.comparing(WeeklyCigaretteStats::weekLabel)) // chronological
                .toList();
    }

    public List<MonthlyCigaretteStats> getMonthlyStats(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<CigaretteEntry> entries = cigaretteEntryRepository.findAllByUserOrderByTimestampDesc(user);

        Map<YearMonth, List<CigaretteEntry>> grouped = entries.stream()
                .collect(Collectors.groupingBy(e ->
                        YearMonth.from(e.getTimestamp())));

        return grouped.entrySet().stream()
                .map(entry -> {
                    YearMonth month = entry.getKey();
                    int total = entry.getValue().size();
                    double avgCraving = entry.getValue().stream()
                            .mapToDouble(CigaretteEntry::getCravingLevel)
                            .average()
                            .orElse(0.0);
                    return new MonthlyCigaretteStats(month, total, avgCraving);
                })
                .sorted(Comparator.comparing(MonthlyCigaretteStats::getMonth))
                .toList();
    }

    public int calculateLongestStreak(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<LocalDate> dates = cigaretteEntryRepository.findAllByUserOrderByTimestampDesc(user).stream()
                .map(e -> e.getTimestamp().toLocalDate())
                .distinct()
                .sorted()
                .toList();

        if (dates.isEmpty()) return 0;

        int longest = 1;
        int current = 1;

        for (int i = 1; i < dates.size(); i++) {
            if (dates.get(i).minusDays(1).equals(dates.get(i - 1))) {
                current++;
                longest = Math.max(longest, current);
            } else {
                current = 1;
            }
        }

        return longest;
    }

    public double getOverallAvgCraving(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return cigaretteEntryRepository.findAllByUserOrderByTimestampDesc(user).stream()
                .mapToDouble(CigaretteEntry::getCravingLevel)
                .average()
                .orElse(0.0);
    }
}
