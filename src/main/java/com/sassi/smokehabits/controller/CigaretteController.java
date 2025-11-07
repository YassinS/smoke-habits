package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.dto.request.LogCigaretteRequest;
import com.sassi.smokehabits.dto.response.CigaretteResponse;
import com.sassi.smokehabits.entity.SmokeContext;
import com.sassi.smokehabits.repository.SmokeContextRepository;
import com.sassi.smokehabits.security.SmokeUserDetails;
import com.sassi.smokehabits.service.CigaretteService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cigarettes")
public class CigaretteController {

    private static final Logger logger = LoggerFactory.getLogger(
        CigaretteController.class
    );
    private final CigaretteService cigaretteService;
    private final SmokeContextRepository smokeContextRepository;

    public CigaretteController(
        CigaretteService cigaretteService,
        SmokeContextRepository smokeContextRepository
    ) {
        this.cigaretteService = cigaretteService;
        this.smokeContextRepository = smokeContextRepository;
    }

    @PostMapping("/log")
    public ResponseEntity<CigaretteResponse> logCigarette(
        Authentication authentication,
        @RequestBody LogCigaretteRequest logRequestBody
    ) {
        SmokeUserDetails userDetails =
            (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();

        CigaretteResponse response;
        if (logRequestBody.getSmokeContext() != null) {
            Optional<SmokeContext> context = smokeContextRepository.findById(
                logRequestBody.getSmokeContext()
            );
            response = context.isPresent()
                ? cigaretteService.logCigarette(
                      userId,
                      logRequestBody.getCravingLevel(),
                      context.get()
                  )
                : cigaretteService.logCigarette(
                      userId,
                      logRequestBody.getCravingLevel()
                  );
        } else {
            response = cigaretteService.logCigarette(
                userId,
                logRequestBody.getCravingLevel()
            );
        }

        logger.info("Cigarette logged for user: {}", userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CigaretteResponse>> getAll(
        Authentication authentication
    ) {
        SmokeUserDetails userDetails =
            (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();

        List<CigaretteResponse> response = cigaretteService.getAll(userId);
        logger.info(
            "Retrieved {} cigarettes for user: {}",
            response.size(),
            userId
        );

        return ResponseEntity.ok(response);
    }
}
