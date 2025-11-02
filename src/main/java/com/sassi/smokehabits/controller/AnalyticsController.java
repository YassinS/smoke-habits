package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.dto.analytics.DailyCigaretteStats;
import com.sassi.smokehabits.dto.analytics.MonthlyCigaretteStats;
import com.sassi.smokehabits.dto.analytics.WeeklyCigaretteStats;
import com.sassi.smokehabits.security.SmokeUserDetails;
import com.sassi.smokehabits.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyCigaretteStats>> getDailyStats(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        UUID userId = getUserId(authentication);
        List<DailyCigaretteStats> stats = analyticsService.getDailyStats(userId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<WeeklyCigaretteStats>> getWeeklyStats(Authentication authentication) {
        UUID userId = getUserId(authentication);
        List<WeeklyCigaretteStats> stats = analyticsService.getWeeklyStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyCigaretteStats>> getMonthlyStats(Authentication authentication) {
        UUID userId = getUserId(authentication);
        List<MonthlyCigaretteStats> stats = analyticsService.getMonthlyStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/longest-streak")
    public ResponseEntity<Integer> getLongestStreak(Authentication authentication) {
        UUID userId = getUserId(authentication);
        int streak = analyticsService.calculateLongestStreak(userId);
        return ResponseEntity.ok(streak);
    }

    @GetMapping("/avg-craving")
    public ResponseEntity<Double> getOverallAvgCraving(Authentication authentication) {
        UUID userId = getUserId(authentication);
        double avgCraving = analyticsService.getOverallAvgCraving(userId);
        return ResponseEntity.ok(avgCraving);
    }

    private UUID getUserId(Authentication authentication) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }
}
