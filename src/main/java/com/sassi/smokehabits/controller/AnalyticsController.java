package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.dto.DailyCigaretteStats;
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
}

