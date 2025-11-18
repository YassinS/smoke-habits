package com.sassi.smokehabits.dto.response;

import java.time.Instant;

public class ReductionProgressResponse {

    private boolean hasActiveGoal;
    private int allowedCigarettesToday;
    private int cigarettesLoggedToday;
    private int cigarettesRemaining;
    private boolean limitExceeded;
    private String message;
    private Instant currentDate;
    private ReductionGoalResponse activeGoal;

    public ReductionProgressResponse() {}

    public ReductionProgressResponse(boolean hasActiveGoal, int allowedCigarettesToday,
                                    int cigarettesLoggedToday, int cigarettesRemaining,
                                    boolean limitExceeded, String message, Instant currentDate,
                                    ReductionGoalResponse activeGoal) {
        this.hasActiveGoal = hasActiveGoal;
        this.allowedCigarettesToday = allowedCigarettesToday;
        this.cigarettesLoggedToday = cigarettesLoggedToday;
        this.cigarettesRemaining = cigarettesRemaining;
        this.limitExceeded = limitExceeded;
        this.message = message;
        this.currentDate = currentDate;
        this.activeGoal = activeGoal;
    }

    // Getters and Setters
    public boolean isHasActiveGoal() {
        return hasActiveGoal;
    }

    public void setHasActiveGoal(boolean hasActiveGoal) {
        this.hasActiveGoal = hasActiveGoal;
    }

    public int getAllowedCigarettesToday() {
        return allowedCigarettesToday;
    }

    public void setAllowedCigarettesToday(int allowedCigarettesToday) {
        this.allowedCigarettesToday = allowedCigarettesToday;
    }

    public int getCigarettesLoggedToday() {
        return cigarettesLoggedToday;
    }

    public void setCigarettesLoggedToday(int cigarettesLoggedToday) {
        this.cigarettesLoggedToday = cigarettesLoggedToday;
    }

    public int getCigarettesRemaining() {
        return cigarettesRemaining;
    }

    public void setCigarettesRemaining(int cigarettesRemaining) {
        this.cigarettesRemaining = cigarettesRemaining;
    }

    public boolean isLimitExceeded() {
        return limitExceeded;
    }

    public void setLimitExceeded(boolean limitExceeded) {
        this.limitExceeded = limitExceeded;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Instant currentDate) {
        this.currentDate = currentDate;
    }

    public ReductionGoalResponse getActiveGoal() {
        return activeGoal;
    }

    public void setActiveGoal(ReductionGoalResponse activeGoal) {
        this.activeGoal = activeGoal;
    }
}
