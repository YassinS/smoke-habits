package com.sassi.smokehabits.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reduction_goals")
public class ReductionGoal {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int startingCigarettesPerDay;

    @Column(nullable = false)
    private int targetCigarettesPerDay;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;

    @Column(nullable = false)
    private int durationInDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReductionStrategy strategy = ReductionStrategy.LINEAR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.ACTIVE;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant completedAt;

    @Column(nullable = false)
    private double dailyReductionRate;

    public enum ReductionStrategy {
        LINEAR,      // Reduce by same amount each day
        STEPPED,     // Reduce in weekly steps
        GRADUAL      // Slower at first, faster later
    }

    public enum GoalStatus {
        ACTIVE,
        COMPLETED,
        ABANDONED,
        PAUSED
    }

    public ReductionGoal() {
        this.createdAt = Instant.now();
    }

    public ReductionGoal(User user, int startingCigarettesPerDay, int targetCigarettesPerDay,
                         Instant startDate, int durationInDays, ReductionStrategy strategy) {
        this.user = user;
        this.startingCigarettesPerDay = startingCigarettesPerDay;
        this.targetCigarettesPerDay = targetCigarettesPerDay;
        this.startDate = startDate;
        this.durationInDays = durationInDays;
        this.endDate = startDate.plus(durationInDays, java.time.temporal.ChronoUnit.DAYS);
        this.strategy = strategy;
        this.status = GoalStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.dailyReductionRate = calculateDailyReductionRate();
    }

    private double calculateDailyReductionRate() {
        int totalReduction = startingCigarettesPerDay - targetCigarettesPerDay;
        return (double) totalReduction / durationInDays;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (dailyReductionRate == 0) {
            dailyReductionRate = calculateDailyReductionRate();
        }
        if (endDate == null && startDate != null) {
            endDate = startDate.plus(durationInDays, java.time.temporal.ChronoUnit.DAYS);
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public ReductionStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ReductionStrategy strategy) {
        this.strategy = strategy;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
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
}
