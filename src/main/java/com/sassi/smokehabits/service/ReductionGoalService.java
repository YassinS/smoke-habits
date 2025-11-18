package com.sassi.smokehabits.service;

import com.sassi.smokehabits.dto.request.CreateReductionGoalRequest;
import com.sassi.smokehabits.dto.response.ReductionGoalResponse;
import com.sassi.smokehabits.dto.response.ReductionProgressResponse;
import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.entity.ReductionGoal;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.exception.ValidationException;
import com.sassi.smokehabits.repository.CigaretteEntryRepository;
import com.sassi.smokehabits.repository.ReductionGoalRepository;
import com.sassi.smokehabits.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReductionGoalService {

    private static final Logger logger = LoggerFactory.getLogger(ReductionGoalService.class);
    private static final int DEFAULT_HISTORY_DAYS = 14;
    private static final int MINIMUM_HISTORY_DAYS = 3;

    private final ReductionGoalRepository reductionGoalRepository;
    private final UserRepository userRepository;
    private final CigaretteEntryRepository cigaretteEntryRepository;

    public ReductionGoalService(ReductionGoalRepository reductionGoalRepository,
                               UserRepository userRepository,
                               CigaretteEntryRepository cigaretteEntryRepository) {
        this.reductionGoalRepository = reductionGoalRepository;
        this.userRepository = userRepository;
        this.cigaretteEntryRepository = cigaretteEntryRepository;
    }

    @Transactional
    public ReductionGoalResponse createGoal(UUID userId, CreateReductionGoalRequest request) {
        User user = userRepository.getUserById(userId);

        // Auto-calculate starting cigarettes from user's recent history
        int startingCigarettes = calculateRecentAverage(user);

        // Validate target is less than starting
        if (request.getTargetCigarettesPerDay() >= startingCigarettes) {
            throw new ValidationException(
                String.format("Target cigarettes per day (%d) must be less than your current average (%d). You're already at or below your target!",
                    request.getTargetCigarettesPerDay(), startingCigarettes)
            );
        }

        // Check if user already has an active goal
        reductionGoalRepository.findActiveGoalByUser(user).ifPresent(existingGoal -> {
            logger.info("User {} already has an active goal. Marking it as abandoned.", userId);
            existingGoal.setStatus(ReductionGoal.GoalStatus.ABANDONED);
            reductionGoalRepository.save(existingGoal);
        });

        Instant startDate = Instant.now();

        ReductionGoal goal = new ReductionGoal(
            user,
            startingCigarettes,
            request.getTargetCigarettesPerDay(),
            startDate,
            request.getDurationInDays(),
            request.getStrategy()
        );

        ReductionGoal savedGoal = reductionGoalRepository.save(goal);
        logger.info("Created reduction goal {} for user {}. Auto-calculated starting: {} cigarettes/day",
            savedGoal.getId(), userId, startingCigarettes);

        return mapToResponse(savedGoal);
    }

    @Transactional(readOnly = true)
    public ReductionGoalResponse getActiveGoal(UUID userId) {
        User user = userRepository.getUserById(userId);
        ReductionGoal goal = reductionGoalRepository.findActiveGoalByUser(user)
            .orElseThrow(() -> new ValidationException("No active reduction goal found"));

        return mapToResponse(goal);
    }

    @Transactional(readOnly = true)
    public List<ReductionGoalResponse> getAllGoals(UUID userId) {
        User user = userRepository.getUserById(userId);
        List<ReductionGoal> goals = reductionGoalRepository.findAllByUserOrderByCreatedAtDesc(user);

        return goals.stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public ReductionProgressResponse getTodayProgress(UUID userId) {
        User user = userRepository.getUserById(userId);
        Instant now = Instant.now();

        // Check if user has an active goal
        var optionalGoal = reductionGoalRepository.findActiveGoalByUser(user);

        if (optionalGoal.isEmpty()) {
            return new ReductionProgressResponse(
                false, 0, 0, 0, false,
                "No active reduction goal", now, null
            );
        }

        ReductionGoal goal = optionalGoal.get();

        // Auto-complete goal if end date has passed
        if (now.isAfter(goal.getEndDate()) && goal.getStatus() == ReductionGoal.GoalStatus.ACTIVE) {
            goal.setStatus(ReductionGoal.GoalStatus.COMPLETED);
            goal.setCompletedAt(Instant.now());
            reductionGoalRepository.save(goal);
            logger.info("Auto-completed goal {} for user {}", goal.getId(), userId);
        }

        int allowedToday = calculateAllowedCigarettes(goal, now);
        int loggedToday = getCigarettesLoggedToday(user);
        int remaining = Math.max(0, allowedToday - loggedToday);
        boolean exceeded = loggedToday > allowedToday;

        String message = buildProgressMessage(allowedToday, loggedToday, remaining, exceeded);

        return new ReductionProgressResponse(
            true,
            allowedToday,
            loggedToday,
            remaining,
            exceeded,
            message,
            now,
            mapToResponse(goal)
        );
    }

    @Transactional
    public ReductionGoalResponse updateGoalStatus(UUID userId, UUID goalId, ReductionGoal.GoalStatus newStatus) {
        User user = userRepository.getUserById(userId);
        ReductionGoal goal = reductionGoalRepository.findById(goalId)
            .orElseThrow(() -> new ValidationException("Reduction goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ValidationException("You don't have permission to modify this goal");
        }

        goal.setStatus(newStatus);

        if (newStatus == ReductionGoal.GoalStatus.COMPLETED || newStatus == ReductionGoal.GoalStatus.ABANDONED) {
            goal.setCompletedAt(Instant.now());
        }

        ReductionGoal updatedGoal = reductionGoalRepository.save(goal);
        logger.info("Updated goal {} status to {} for user {}", goalId, newStatus, userId);

        return mapToResponse(updatedGoal);
    }

    /**
     * Calculate the user's recent average cigarettes per day from their logged history
     */
    private int calculateRecentAverage(User user) {
        List<CigaretteEntry> allEntries = cigaretteEntryRepository.findAllByUserOrderByTimestampDesc(user);

        if (allEntries.isEmpty()) {
            throw new ValidationException(
                "You need to log cigarettes for at least " + MINIMUM_HISTORY_DAYS + " days before creating a reduction goal. " +
                "This helps us understand your current smoking habits."
            );
        }

        Instant now = Instant.now();
        Instant cutoffDate = now.minus(DEFAULT_HISTORY_DAYS, ChronoUnit.DAYS);

        // Get entries from the last DEFAULT_HISTORY_DAYS days
        List<CigaretteEntry> recentEntries = allEntries.stream()
            .filter(entry -> entry.getTimestamp().isAfter(cutoffDate))
            .toList();

        if (recentEntries.isEmpty()) {
            throw new ValidationException(
                "No recent cigarette logs found. Please log cigarettes for at least " + MINIMUM_HISTORY_DAYS + " days."
            );
        }

        // Group by day and count cigarettes per day
        Map<String, Long> cigarettesPerDay = recentEntries.stream()
            .collect(Collectors.groupingBy(
                entry -> entry.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate().toString(),
                Collectors.counting()
            ));

        // Check if we have enough days of data
        if (cigarettesPerDay.size() < MINIMUM_HISTORY_DAYS) {
            throw new ValidationException(
                String.format("You've only logged cigarettes on %d day(s). Please log for at least %d days to establish a baseline.",
                    cigarettesPerDay.size(), MINIMUM_HISTORY_DAYS)
            );
        }

        // Calculate average per day
        double averagePerDay = cigarettesPerDay.values().stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);

        // Round up to nearest integer (conservative estimate)
        int roundedAverage = (int) Math.ceil(averagePerDay);

        logger.info("Calculated average for user {}: {} cigarettes/day over {} days",
            user.getId(), roundedAverage, cigarettesPerDay.size());

        return Math.max(1, roundedAverage); // Ensure at least 1
    }

    /**
     * Calculate the allowed number of cigarettes for a specific date based on reduction strategy
     */
    private int calculateAllowedCigarettes(ReductionGoal goal, Instant date) {
        // If date is before start date, no limit applies
        if (date.isBefore(goal.getStartDate())) {
            return Integer.MAX_VALUE;
        }

        // If date is after end date, use target
        if (date.isAfter(goal.getEndDate())) {
            return goal.getTargetCigarettesPerDay();
        }

        long daysElapsed = ChronoUnit.DAYS.between(goal.getStartDate(), date);

        return switch (goal.getStrategy()) {
            case LINEAR -> calculateLinearReduction(goal, daysElapsed);
            case STEPPED -> calculateSteppedReduction(goal, daysElapsed);
            case GRADUAL -> calculateGradualReduction(goal, daysElapsed);
        };
    }

    private int calculateLinearReduction(ReductionGoal goal, long daysElapsed) {
        double reduction = goal.getDailyReductionRate() * daysElapsed;
        int allowed = (int) Math.round(goal.getStartingCigarettesPerDay() - reduction);
        return Math.max(goal.getTargetCigarettesPerDay(), allowed);
    }

    private int calculateSteppedReduction(ReductionGoal goal, long daysElapsed) {
        // Reduce in weekly steps
        int weekElapsed = (int) (daysElapsed / 7);
        int totalWeeks = goal.getDurationInDays() / 7;

        if (totalWeeks == 0) {
            return goal.getTargetCigarettesPerDay();
        }

        double reductionPerWeek = (double) (goal.getStartingCigarettesPerDay() - goal.getTargetCigarettesPerDay()) / totalWeeks;
        int allowed = (int) Math.round(goal.getStartingCigarettesPerDay() - (reductionPerWeek * weekElapsed));

        return Math.max(goal.getTargetCigarettesPerDay(), allowed);
    }

    private int calculateGradualReduction(ReductionGoal goal, long daysElapsed) {
        // Slower reduction at first, accelerates towards the end
        double progress = (double) daysElapsed / goal.getDurationInDays();
        double acceleratedProgress = Math.pow(progress, 1.5); // Exponential curve

        int totalReduction = goal.getStartingCigarettesPerDay() - goal.getTargetCigarettesPerDay();
        int currentReduction = (int) Math.round(totalReduction * acceleratedProgress);

        return Math.max(goal.getTargetCigarettesPerDay(), goal.getStartingCigarettesPerDay() - currentReduction);
    }

    private int getCigarettesLoggedToday(User user) {
        Instant now = Instant.now();
        Instant startOfDay = now.atZone(ZoneId.systemDefault()).toLocalDate()
            .atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);

        List<CigaretteEntry> entries = cigaretteEntryRepository.findAllByUserOrderByTimestampDesc(user);

        return (int) entries.stream()
            .filter(entry -> !entry.getTimestamp().isBefore(startOfDay) && entry.getTimestamp().isBefore(endOfDay))
            .count();
    }

    private String buildProgressMessage(int allowed, int logged, int remaining, boolean exceeded) {
        if (exceeded) {
            int overLimit = logged - allowed;
            return String.format("You've exceeded your daily limit by %d cigarette%s. Tomorrow is a new day!",
                overLimit, overLimit == 1 ? "" : "s");
        } else if (remaining == 0) {
            return "You've reached your limit for today. Stay strong!";
        } else if (remaining == 1) {
            return "You have 1 cigarette remaining for today.";
        } else {
            return String.format("You have %d cigarettes remaining for today.", remaining);
        }
    }

    private ReductionGoalResponse mapToResponse(ReductionGoal goal) {
        Instant now = Instant.now();
        long daysElapsed = Math.max(0, ChronoUnit.DAYS.between(goal.getStartDate(), now));
        long daysRemaining = Math.max(0, ChronoUnit.DAYS.between(now, goal.getEndDate()));

        int currentDayLimit = calculateAllowedCigarettes(goal, now);
        double progressPercentage = goal.getDurationInDays() > 0 ?
            (daysElapsed * 100.0 / goal.getDurationInDays()) : 0;
        progressPercentage = Math.min(100, progressPercentage);

        return new ReductionGoalResponse(
            goal.getId(),
            goal.getStartingCigarettesPerDay(),
            goal.getTargetCigarettesPerDay(),
            goal.getStartDate(),
            goal.getEndDate(),
            goal.getDurationInDays(),
            goal.getStrategy(),
            goal.getStatus(),
            goal.getCreatedAt(),
            goal.getCompletedAt(),
            goal.getDailyReductionRate(),
            currentDayLimit,
            daysElapsed,
            daysRemaining,
            progressPercentage
        );
    }
}
