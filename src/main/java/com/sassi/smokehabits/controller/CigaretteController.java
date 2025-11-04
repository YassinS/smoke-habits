package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.dto.response.CigaretteResponse;
import com.sassi.smokehabits.dto.request.LogCigaretteRequest;
import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.entity.SmokeContext;
import com.sassi.smokehabits.repository.SmokeContextRepository;
import com.sassi.smokehabits.security.SmokeUserDetails;
import com.sassi.smokehabits.service.CigaretteService;
import jdk.management.jfr.EventTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/cigarettes")
public class CigaretteController {

    private static final Logger logger = LoggerFactory.getLogger(CigaretteController.class);
    private final CigaretteService cigaretteService;
    private final SmokeContextRepository smokeContextRepository;

    public CigaretteController(CigaretteService cigaretteService,  SmokeContextRepository smokeContextRepository) {
        this.cigaretteService = cigaretteService;
        this.smokeContextRepository = smokeContextRepository;
        logger.info("CigaretteController initialized");
    }

    @PostMapping("/log")
    public ResponseEntity<CigaretteResponse> logCigarette(Authentication authentication,
                                                          @RequestBody LogCigaretteRequest logRequestBody) {
        logger.info("=== CigaretteController.logCigarette() called ===");
        try {
            CigaretteEntry entry;
            logger.info("Step 1: Getting user details from authentication");
            SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();

            UUID userId = userDetails.getUserId();
            logger.info("Logging cigarette for user: {} with craving level: {}", userId, logRequestBody.getCravingLevel());
            
            logger.info("Step 2: Checking for SmokeContext");
            if (logRequestBody.getSmokeContext() != null) {
                logger.info("SmokeContext ID provided: {}", logRequestBody.getSmokeContext());
                Optional<SmokeContext> context = smokeContextRepository.findById(logRequestBody.getSmokeContext());
                if (context.isPresent()) {
                    logger.info("SmokeContext found, logging cigarette with context");
                    entry = cigaretteService.logCigarette(userId, logRequestBody.getCravingLevel(), context.get());
                } else {
                    logger.warn("SmokeContext not found with ID: {}, logging without context", logRequestBody.getSmokeContext());
                    entry = cigaretteService.logCigarette(userId, logRequestBody.getCravingLevel());
                }
            } else {
                logger.info("No SmokeContext provided, logging without context");
                entry = cigaretteService.logCigarette(userId, logRequestBody.getCravingLevel());
            }
            
            logger.info("Successfully logged cigarette with id: {} for user: {}", entry.getId(), userId);
            
            logger.info("Step 3: Creating response and returning");
            CigaretteResponse response;
            if (entry.getContext() != null) {
                response = new CigaretteResponse(entry.getId(), entry.getTimestamp(),entry.getCravingLevel(), entry.getContext().toSmokeContextResponse());
            } else {
                response = new CigaretteResponse(entry.getId(), entry.getTimestamp(), entry.getCravingLevel());
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("ERROR in CigaretteController.logCigarette(): {}", e.getMessage(), e);
            throw e;
        }
    }


    @GetMapping
    public ResponseEntity<List<CigaretteResponse>> getAll(Authentication authentication) {
        logger.info("=== CigaretteController.getAll() called ===");
        try {
            logger.info("Step 1: Getting user details from authentication");
            SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
            
            logger.info("Step 2: Extracting user ID");
            UUID userId = userDetails.getUserId();
            logger.info("Fetching cigarettes for user: {}", userId);
            
            logger.info("Step 3: Calling cigaretteService.getAll()");
            List<CigaretteEntry> cigaretteEntries = cigaretteService.getAll(userId);
            logger.info("Found {} cigarette entries", cigaretteEntries.size());
            
            logger.info("Step 4: Mapping to response DTOs");
            List<CigaretteResponse> response = cigaretteEntries.stream()
                    .map(entry -> new CigaretteResponse(
                            entry.getId(),
                            entry.getTimestamp(),
                            entry.getCravingLevel(),
                            (entry.getContext() == null) ? null : entry.getContext().toSmokeContextResponse())
                    ).toList();
            
            logger.info("Step 5: Returning response with {} entries", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("ERROR in CigaretteController.getAll(): {}", e.getMessage(), e);
            throw e;
        }
    }
}

