package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.dto.request.CreateReductionGoalRequest;
import com.sassi.smokehabits.dto.response.ReductionGoalResponse;
import com.sassi.smokehabits.dto.response.ReductionProgressResponse;
import com.sassi.smokehabits.entity.ReductionGoal;
import com.sassi.smokehabits.security.SmokeUserDetails;
import com.sassi.smokehabits.service.ReductionGoalService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reduction-goals")
public class ReductionGoalController {

    private static final Logger logger = LoggerFactory.getLogger(
        ReductionGoalController.class
    );

    private final ReductionGoalService reductionGoalService;

    public ReductionGoalController(ReductionGoalService reductionGoalService) {
        this.reductionGoalService = reductionGoalService;
    }

    @PostMapping
    public ResponseEntity<ReductionGoalResponse> createGoal(
        Authentication authentication,
        @Valid @RequestBody CreateReductionGoalRequest request
    ) {
        SmokeUserDetails userDetails =
            (SmokeUserDetails) authentication.getPrincipal();
        logger.info(
            "User {} creating new reduction goal",
            userDetails.getUserId()
        );

        ReductionGoalResponse response = reductionGoalService.createGoal(
            userDetails.getUserId(),
            request
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ReductionGoalResponse> getActiveGoal(
        Authentication authentication
    ) {
        SmokeUserDetails userDetails =
            (SmokeUserDetails) authentication.getPrincipal();
        logger.debug(
            "User {} fetching active reduction goal",
            userDetails.getUserId()
        );

        ReductionGoalResponse response = reductionGoalService.getActiveGoal(
            userDetails.getUserId()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReductionGoalResponse>> getAllGoals(
        Authentication authentication
    ) {
        SmokeUserDetails userDetails =
            (SmokeUserDetails) authentication.getPrincipal();
        logger.debug(
            "User {} fetching all reduction goals",
            userDetails.getUserId()
        );

        List<ReductionGoalResponse> response = reductionGoalService.getAllGoals(
            userDetails.getUserId()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/progress")
    public ResponseEntity<ReductionProgressResponse> getTodayProgress(
        Authentication authentication
    ) {
        SmokeUserDetails userDetails =
            (SmokeUserDetails) authentication.getPrincipal();
        logger.debug(
            "User {} checking today's progress",
            userDetails.getUserId()
        );

        ReductionProgressResponse response =
            reductionGoalService.getTodayProgress(userDetails.getUserId());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{goalId}/status")
    public ResponseEntity<ReductionGoalResponse> updateGoalStatus(
        Authentication authentication,
        @PathVariable UUID goalId,
        @RequestBody Map<String, String> statusUpdate
    ) {
        SmokeUserDetails userDetails =
            (SmokeUserDetails) authentication.getPrincipal();
        String statusStr = statusUpdate.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }

        ReductionGoal.GoalStatus status;
        try {
            status = ReductionGoal.GoalStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid status value: {}", statusStr);
            return ResponseEntity.badRequest().build();
        }

        logger.info(
            "User {} updating goal {} to status {}",
            userDetails.getUserId(),
            goalId,
            status
        );

        ReductionGoalResponse response = reductionGoalService.updateGoalStatus(
            userDetails.getUserId(),
            goalId,
            status
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/strategies")
    public ResponseEntity<Map<String, String>> getAvailableStrategies() {
        return ResponseEntity.ok(
            Map.of(
                "LINEAR",
                "Reduce by the same amount each day - steady and predictable",
                "STEPPED",
                "Reduce in weekly steps - gives you time to adjust",
                "GRADUAL",
                "Start slow, then accelerate - easier at the beginning"
            )
        );
    }
}
