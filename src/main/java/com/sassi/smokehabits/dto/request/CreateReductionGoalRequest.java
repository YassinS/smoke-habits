package com.sassi.smokehabits.dto.request;

import com.sassi.smokehabits.entity.ReductionGoal;
import jakarta.validation.constraints.*;

public class CreateReductionGoalRequest {

    @NotNull(message = "Target cigarettes per day is required")
    @Min(value = 0, message = "Target cigarettes per day must be at least 0")
    @Max(value = 100, message = "Target cigarettes per day must not exceed 100")
    private Integer targetCigarettesPerDay;

    @NotNull(message = "Duration in days is required")
    @Min(value = 7, message = "Duration must be at least 7 days")
    @Max(value = 365, message = "Duration must not exceed 365 days")
    private Integer durationInDays;

    @Min(value = 1, message = "Starting cigarettes per day must be at least 1")
    @Max(
        value = 100,
        message = "Starting cigarettes per day must not exceed 100"
    )
    private Integer customStartingCigarettesPerDay;

    private ReductionGoal.ReductionStrategy strategy =
        ReductionGoal.ReductionStrategy.LINEAR;

    public CreateReductionGoalRequest() {}

    public CreateReductionGoalRequest(
        Integer targetCigarettesPerDay,
        Integer durationInDays,
        ReductionGoal.ReductionStrategy strategy
    ) {
        this.targetCigarettesPerDay = targetCigarettesPerDay;
        this.durationInDays = durationInDays;
        this.strategy = strategy;
        this.customStartingCigarettesPerDay = null;
    }

    public CreateReductionGoalRequest(
        Integer targetCigarettesPerDay,
        Integer durationInDays,
        Integer customStartingCigarettesPerDay,
        ReductionGoal.ReductionStrategy strategy
    ) {
        this.targetCigarettesPerDay = targetCigarettesPerDay;
        this.durationInDays = durationInDays;
        this.customStartingCigarettesPerDay = customStartingCigarettesPerDay;
        this.strategy = strategy;
    }

    // Getters and Setters
    public Integer getTargetCigarettesPerDay() {
        return targetCigarettesPerDay;
    }

    public void setTargetCigarettesPerDay(Integer targetCigarettesPerDay) {
        this.targetCigarettesPerDay = targetCigarettesPerDay;
    }

    public Integer getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(Integer durationInDays) {
        this.durationInDays = durationInDays;
    }

    public Integer getCustomStartingCigarettesPerDay() {
        return customStartingCigarettesPerDay;
    }

    public void setCustomStartingCigarettesPerDay(
        Integer customStartingCigarettesPerDay
    ) {
        this.customStartingCigarettesPerDay = customStartingCigarettesPerDay;
    }

    public ReductionGoal.ReductionStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ReductionGoal.ReductionStrategy strategy) {
        this.strategy = strategy;
    }
}
