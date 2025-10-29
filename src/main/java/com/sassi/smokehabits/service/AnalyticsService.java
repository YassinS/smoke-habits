package com.sassi.smokehabits.service;

import com.sassi.smokehabits.dto.DailyCigaretteStats;
import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.repository.CigaretteEntryRepository;
import com.sassi.smokehabits.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        // group by day
        Map<LocalDate, List<CigaretteEntry>> grouped = entries.stream()
                .collect(Collectors.groupingBy(e -> e.getTimestamp().toLocalDate()));

        // build initial daily stats
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
}
