package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.dto.analytics.DailyCigaretteStats;
import com.sassi.smokehabits.dto.analytics.MonthlyCigaretteStats;
import com.sassi.smokehabits.dto.analytics.WeeklyCigaretteStats;
import com.sassi.smokehabits.security.SmokeUserDetails;
import com.sassi.smokehabits.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<DailyCigaretteStats>> getDailyStats(Authentication authentication) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();

        List<DailyCigaretteStats> stats = analyticsService.getDailyStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<WeeklyCigaretteStats>> getWeeklyStats(Authentication authentication) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        return ResponseEntity.ok(analyticsService.getWeeklyStats(userId));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyCigaretteStats>> getMonthlyStats(Authentication authentication) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        List<MonthlyCigaretteStats> stats = analyticsService.getMonthlyStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/longest-streak")
    public ResponseEntity<Integer> getLongestStreak(Authentication authentication) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        int streak = analyticsService.calculateLongestStreak(userId);
        return ResponseEntity.ok(streak);
    }

    @GetMapping("/avg-craving")
    public ResponseEntity<Double> getOverallAvgCraving(Authentication authentication) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        double avgCraving = analyticsService.getOverallAvgCraving(userId);
        return ResponseEntity.ok(avgCraving);
    }



}

