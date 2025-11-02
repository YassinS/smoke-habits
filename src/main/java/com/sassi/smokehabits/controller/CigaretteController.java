package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.dto.response.CigaretteResponse;
import com.sassi.smokehabits.dto.request.LogCigaretteRequest;
import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.security.SmokeUserDetails;
import com.sassi.smokehabits.service.CigaretteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cigarettes")
public class CigaretteController {

    private static final Logger logger = LoggerFactory.getLogger(CigaretteController.class);
    private final CigaretteService cigaretteService;

    public CigaretteController(CigaretteService cigaretteService) {
        this.cigaretteService = cigaretteService;
    }

    @PostMapping("/log")
    public ResponseEntity<CigaretteResponse> logCigarette(Authentication authentication,
                                                          @RequestBody LogCigaretteRequest logRequestBody) {
        try {
            SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
            UUID userId = userDetails.getUserId();
            logger.info("Logging cigarette for user: {} with craving level: {}", userId, logRequestBody.getCravingLevel());
            
            CigaretteEntry entry = cigaretteService.logCigarette(userId, logRequestBody.getCravingLevel());
            logger.info("Successfully logged cigarette with id: {} for user: {}", entry.getId(), userId);
            
            CigaretteResponse response = new CigaretteResponse(entry.getId(), entry.getTimestamp());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error logging cigarette", e);
            throw e;
        }
    }


    @GetMapping
    public ResponseEntity<List<CigaretteResponse>> getAll(Authentication authentication) {
        try {
            SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
            UUID userId = userDetails.getUserId();
            logger.info("Fetching all cigarettes for user: {}", userId);
            
            List<CigaretteEntry> cigaretteEntries = cigaretteService.getAll(userId);
            logger.info("Found {} cigarette entries for user: {}", cigaretteEntries.size(), userId);
            
            List<CigaretteResponse> response = cigaretteEntries.stream()
                    .map(entry -> new CigaretteResponse(
                            entry.getId(),
                            entry.getTimestamp())
                    ).toList();
            
            logger.info("Successfully returning {} cigarette responses", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching cigarettes", e);
            throw e;
        }
    }
}

