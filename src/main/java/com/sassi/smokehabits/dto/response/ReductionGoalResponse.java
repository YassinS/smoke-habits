package com.sassi.smokehabits.dto.response;

import com.sassi.smokehabits.entity.ReductionGoal;

import java.time.Instant;
import java.util.UUID;

public class ReductionGoalResponse {

    private UUID id;
    private int startingCigarettesPerDay;
    private int targetCigarettesPerDay;
    private Instant startDate;
    private Instant endDate;
    private int durationInDays;
    private ReductionGoal.ReductionStrategy strategy;
    private ReductionGoal.GoalStatus status;
    private Instant createdAt;
    private Instant completedAt;
    private double dailyReductionRate;
    private int currentDayLimit;
    private long daysElapsed;
    private long daysRemaining;
    private double progressPercentage;

    public ReductionGoalResponse() {}

    public ReductionGoalResponse(UUID id, int startingCigarettesPerDay, int targetCigarettesPerDay,
                                Instant startDate, Instant endDate, int durationInDays,
                                ReductionGoal.ReductionStrategy strategy, ReductionGoal.GoalStatus status,
                                Instant createdAt, Instant completedAt, double dailyReductionRate,
                                int currentDayLimit, long daysElapsed, long daysRemaining, double progressPercentage) {
        this.id = id;
        this.startingCigarettesPerDay = startingCigarettesPerDay;
        this.targetCigarettesPerDay = targetCigarettesPerDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationInDays = durationInDays;
        this.strategy = strategy;
        this.status = status;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.dailyReductionRate = dailyReductionRate;
        this.currentDayLimit = currentDayLimit;
        this.daysElapsed = daysElapsed;
        this.daysRemaining = daysRemaining;
        this.progressPercentage = progressPercentage;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getStartingCigarettesPerDay() {
        return startingCigarettesPerDay;
    }

    public void setStartingCigarettesPerDay(int startingCigarettesPerDay) {
        this.startingCigarettesPerDay = startingCigarettesPerDay;
    }

    public int getTargetCigarettesPerDay() {
        return targetCigarettesPerDay;
    }

    public void setTargetCigarettesPerDay(int targetCigarettesPerDay) {
        this.targetCigarettesPerDay = targetCigarettesPerDay;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

    public ReductionGoal.ReductionStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ReductionGoal.ReductionStrategy strategy) {
        this.strategy = strategy;
    }

    public ReductionGoal.GoalStatus getStatus() {
        return status;
    }

    public void setStatus(ReductionGoal.GoalStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public double getDailyReductionRate() {
        return dailyReductionRate;
    }

    public void setDailyReductionRate(double dailyReductionRate) {
        this.dailyReductionRate = dailyReductionRate;
    }

    public int getCurrentDayLimit() {
        return currentDayLimit;
    }

    public void setCurrentDayLimit(int currentDayLimit) {
        this.currentDayLimit = currentDayLimit;
    }

    public long getDaysElapsed() {
        return daysElapsed;
    }

    public void setDaysElapsed(long daysElapsed) {
        this.daysElapsed = daysElapsed;
    }

    public long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}
