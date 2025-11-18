package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.repository.CigaretteEntryRepository;
import com.sassi.smokehabits.repository.UserRepository;
import com.sassi.smokehabits.security.SmokeUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Development-only controller for seeding test data.
 * Only active when spring.profiles.active=dev
 */
@RestController
@RequestMapping("/dev/data")
@Profile({"dev", "local"})
public class DevDataController {

    private static final Logger logger = LoggerFactory.getLogger(DevDataController.class);

    private final CigaretteEntryRepository cigaretteEntryRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    public DevDataController(
        CigaretteEntryRepository cigaretteEntryRepository,
        UserRepository userRepository
    ) {
        this.cigaretteEntryRepository = cigaretteEntryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Seed realistic cigarette data for the past N days
     *
     * Usage: POST /dev/data/seed-cigarettes?days=14&avgPerDay=18
     */
    @PostMapping("/seed-cigarettes")
    public ResponseEntity<Map<String, Object>> seedCigarettes(
        Authentication authentication,
        @RequestParam(defaultValue = "14") int days,
        @RequestParam(defaultValue = "18") int avgPerDay
    ) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        User user = userRepository.getUserById(userDetails.getUserId());

        logger.info("Seeding {} days of cigarette data for user {} (avg: {}/day)",
            days, user.getId(), avgPerDay);

        List<CigaretteEntry> entries = new ArrayList<>();
        Instant now = Instant.now();
        int totalCreated = 0;

        // Create entries for each day going backwards
        for (int dayOffset = days - 1; dayOffset >= 0; dayOffset--) {
            Instant dayStart = now.minus(dayOffset, ChronoUnit.DAYS);

            // Vary the daily count around the average (±3 cigarettes)
            int cigarettesThisDay = avgPerDay + random.nextInt(7) - 3;
            cigarettesThisDay = Math.max(1, cigarettesThisDay); // At least 1

            // Distribute cigarettes throughout the day
            for (int i = 0; i < cigarettesThisDay; i++) {
                // Random hour between 8 AM and 11 PM
                int randomHour = 8 + random.nextInt(15);
                int randomMinute = random.nextInt(60);

                Instant timestamp = dayStart
                    .plus(randomHour, ChronoUnit.HOURS)
                    .plus(randomMinute, ChronoUnit.MINUTES);

                // Random craving level 1-5
                int cravingLevel = 1 + random.nextInt(5);

                CigaretteEntry entry = new CigaretteEntry(user, cravingLevel);
                entry.setTimestamp(timestamp);
                entries.add(entry);
                totalCreated++;
            }
        }

        // Save all entries
        cigaretteEntryRepository.saveAll(entries);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully seeded cigarette data");
        response.put("daysSeeded", days);
        response.put("totalCigarettesCreated", totalCreated);
        response.put("averagePerDay", (double) totalCreated / days);
        response.put("userId", user.getId());

        logger.info("Created {} cigarette entries over {} days", totalCreated, days);

        return ResponseEntity.ok(response);
    }

    /**
     * Seed data with varying patterns (increasing/decreasing trend)
     *
     * Usage: POST /dev/data/seed-with-trend?days=30&start=25&end=15
     */
    @PostMapping("/seed-with-trend")
    public ResponseEntity<Map<String, Object>> seedWithTrend(
        Authentication authentication,
        @RequestParam(defaultValue = "30") int days,
        @RequestParam(defaultValue = "25") int start,
        @RequestParam(defaultValue = "15") int end
    ) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        User user = userRepository.getUserById(userDetails.getUserId());

        logger.info("Seeding {} days with trend {} -> {} for user {}",
            days, start, end, user.getId());

        List<CigaretteEntry> entries = new ArrayList<>();
        Instant now = Instant.now();
        int totalCreated = 0;

        double dailyChange = (double) (end - start) / days;

        for (int dayOffset = days - 1; dayOffset >= 0; dayOffset--) {
            Instant dayStart = now.minus(dayOffset, ChronoUnit.DAYS);

            // Calculate target for this day based on linear trend
            int targetForDay = (int) Math.round(start + dailyChange * (days - dayOffset - 1));

            // Add some randomness (±2)
            int cigarettesThisDay = targetForDay + random.nextInt(5) - 2;
            cigarettesThisDay = Math.max(1, cigarettesThisDay);

            for (int i = 0; i < cigarettesThisDay; i++) {
                int randomHour = 8 + random.nextInt(15);
                int randomMinute = random.nextInt(60);

                Instant timestamp = dayStart
                    .plus(randomHour, ChronoUnit.HOURS)
                    .plus(randomMinute, ChronoUnit.MINUTES);

                int cravingLevel = 1 + random.nextInt(5);

                CigaretteEntry entry = new CigaretteEntry(user, cravingLevel);
                entry.setTimestamp(timestamp);
                entries.add(entry);
                totalCreated++;
            }
        }

        cigaretteEntryRepository.saveAll(entries);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully seeded cigarette data with trend");
        response.put("daysSeeded", days);
        response.put("startingAverage", start);
        response.put("endingAverage", end);
        response.put("totalCigarettesCreated", totalCreated);
        response.put("averagePerDay", (double) totalCreated / days);
        response.put("trend", start > end ? "decreasing" : "increasing");

        return ResponseEntity.ok(response);
    }

    /**
     * Clear all cigarette entries for the authenticated user
     *
     * Usage: DELETE /dev/data/clear-cigarettes
     */
    @DeleteMapping("/clear-cigarettes")
    public ResponseEntity<Map<String, Object>> clearCigarettes(
        Authentication authentication
    ) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        User user = userRepository.getUserById(userDetails.getUserId());

        List<CigaretteEntry> entries = cigaretteEntryRepository
            .findAllByUserOrderByTimestampDesc(user);

        int count = entries.size();
        cigaretteEntryRepository.deleteAll(entries);

        logger.warn("Deleted {} cigarette entries for user {}", count, user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cigarette data cleared");
        response.put("entriesDeleted", count);
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * Get statistics about current cigarette data
     *
     * Usage: GET /dev/data/cigarette-stats
     */
    @GetMapping("/cigarette-stats")
    public ResponseEntity<Map<String, Object>> getCigaretteStats(
        Authentication authentication
    ) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        User user = userRepository.getUserById(userDetails.getUserId());

        List<CigaretteEntry> allEntries = cigaretteEntryRepository
            .findAllByUserOrderByTimestampDesc(user);

        if (allEntries.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "message", "No cigarette data found",
                "totalEntries", 0
            ));
        }

        // Calculate date range
        Instant oldest = allEntries.get(allEntries.size() - 1).getTimestamp();
        Instant newest = allEntries.get(0).getTimestamp();
        long daysCovered = ChronoUnit.DAYS.between(oldest, newest) + 1;

        // Calculate average per day
        Map<String, Long> entriesPerDay = new HashMap<>();
        for (CigaretteEntry entry : allEntries) {
            String day = entry.getTimestamp().toString().substring(0, 10);
            entriesPerDay.merge(day, 1L, Long::sum);
        }

        double avgPerDay = entriesPerDay.values().stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);

        Map<String, Object> response = new HashMap<>();
        response.put("totalEntries", allEntries.size());
        response.put("daysCovered", daysCovered);
        response.put("daysWithEntries", entriesPerDay.size());
        response.put("averagePerDay", Math.round(avgPerDay * 100.0) / 100.0);
        response.put("oldestEntry", oldest);
        response.put("newestEntry", newest);
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * Create a complete test scenario:
     * 1. Seed 14 days of cigarette data
     * 2. Create a reduction goal
     * 3. Add some cigarettes for today
     *
     * Usage: POST /dev/data/create-test-scenario
     */
    @PostMapping("/create-test-scenario")
    public ResponseEntity<Map<String, Object>> createTestScenario(
        Authentication authentication,
        @RequestParam(defaultValue = "18") int avgPerDay,
        @RequestParam(defaultValue = "5") int targetPerDay
    ) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        User user = userRepository.getUserById(userDetails.getUserId());

        logger.info("Creating complete test scenario for user {}", user.getId());

        // 1. Clear existing data
        List<CigaretteEntry> existing = cigaretteEntryRepository
            .findAllByUserOrderByTimestampDesc(user);
        cigaretteEntryRepository.deleteAll(existing);

        // 2. Seed 14 days of data
        List<CigaretteEntry> entries = new ArrayList<>();
        Instant now = Instant.now();
        int totalCreated = 0;

        for (int dayOffset = 14; dayOffset >= 1; dayOffset--) {
            Instant dayStart = now.minus(dayOffset, ChronoUnit.DAYS);
            int cigarettesThisDay = avgPerDay + random.nextInt(5) - 2;
            cigarettesThisDay = Math.max(1, cigarettesThisDay);

            for (int i = 0; i < cigarettesThisDay; i++) {
                int randomHour = 8 + random.nextInt(15);
                int randomMinute = random.nextInt(60);

                Instant timestamp = dayStart
                    .plus(randomHour, ChronoUnit.HOURS)
                    .plus(randomMinute, ChronoUnit.MINUTES);

                CigaretteEntry entry = new CigaretteEntry(user, 1 + random.nextInt(5));
                entry.setTimestamp(timestamp);
                entries.add(entry);
                totalCreated++;
            }
        }

        // 3. Add a few for today (so they can test progress tracking)
        int todayCigarettes = Math.max(1, avgPerDay / 2);
        for (int i = 0; i < todayCigarettes; i++) {
            int randomHour = 8 + random.nextInt(8); // Morning/afternoon only
            int randomMinute = random.nextInt(60);

            Instant timestamp = now
                .minus(10, ChronoUnit.HOURS) // Ensure it's today
                .plus(randomHour, ChronoUnit.HOURS)
                .plus(randomMinute, ChronoUnit.MINUTES);

            CigaretteEntry entry = new CigaretteEntry(user, 1 + random.nextInt(5));
            entry.setTimestamp(timestamp);
            entries.add(entry);
            totalCreated++;
        }

        cigaretteEntryRepository.saveAll(entries);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test scenario created successfully");
        response.put("pastDaysSeeded", 14);
        response.put("cigarettesToday", todayCigarettes);
        response.put("totalCigarettesCreated", totalCreated);
        response.put("calculatedAverage", (double) (totalCreated - todayCigarettes) / 14);
        response.put("nextSteps", Map.of(
            "1", "Check stats: GET /dev/data/cigarette-stats",
            "2", "Create goal: POST /reduction-goals with targetCigarettesPerDay=" + targetPerDay,
            "3", "Check progress: GET /reduction-goals/progress"
        ));

        return ResponseEntity.ok(response);
    }
}
