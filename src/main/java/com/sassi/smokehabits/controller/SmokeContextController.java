package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.dto.request.SmokeContextRequest;
import com.sassi.smokehabits.dto.response.SmokeContextDeletionResponse;
import com.sassi.smokehabits.dto.response.SmokeContextResponse;
import com.sassi.smokehabits.entity.SmokeContext;
import com.sassi.smokehabits.exception.AuthenticationError;
import com.sassi.smokehabits.repository.SmokeContextRepository;
import com.sassi.smokehabits.security.SmokeUserDetails;
import com.sassi.smokehabits.service.SmokeContextService;
import com.sassi.smokehabits.validation.ValidationUtils;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/context")
public class SmokeContextController {

    private static final Logger log = LoggerFactory.getLogger(
        SmokeContextController.class
    );
    private final SmokeContextService smokeContextService;
    private final SmokeContextRepository smokeContextRepository;

    public SmokeContextController(
        SmokeContextService smokeContextService,
        SmokeContextRepository smokeContextRepository
    ) {
        this.smokeContextService = smokeContextService;
        this.smokeContextRepository = smokeContextRepository;
    }

    @GetMapping("/{id}")
    public SmokeContextResponse getSmokeContext(@PathVariable String id) {
        String validatedId = ValidationUtils.validateUUID(id);
        return smokeContextService.findById(UUID.fromString(validatedId));
    }

    @PostMapping("/{id}/edit")
    public SmokeContextResponse editSmokeContext(
        @PathVariable String id,
        @RequestBody SmokeContextRequest smokeContextRequest
    ) {
        String validatedId = ValidationUtils.validateUUID(id);
        UUID contextId = UUID.fromString(validatedId);

        String validatedLabel = ValidationUtils.validateAndSanitizeContextLabel(
            smokeContextRequest.getContext()
        );
        String validatedColor = ValidationUtils.validateAndSanitizeHexColor(
            smokeContextRequest.getColorUI()
        );

        SmokeContext smokeContext = smokeContextRepository
            .findById(contextId)
            .orElseThrow(() ->
                new AuthenticationError("Smoke context not found")
            );

        log.debug("Editing context {}", contextId);

        smokeContextRequest.setContext(validatedLabel);
        smokeContextRequest.setColorUI(validatedColor);

        return smokeContextService.editSmokeContext(
            smokeContext,
            smokeContextRequest
        );
    }

    @GetMapping
    public List<SmokeContextResponse> findAll(Authentication authentication) {
        SmokeUserDetails userDetails =
            (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        return smokeContextService.findAllByUUID(userId);
    }

    @PostMapping
    public SmokeContextResponse create(
        Authentication authentication,
        @RequestBody SmokeContextRequest smokeContextRequest
    ) {
        String validatedLabel = ValidationUtils.validateAndSanitizeContextLabel(
            smokeContextRequest.getContext()
        );
        String validatedColor = ValidationUtils.validateAndSanitizeHexColor(
            smokeContextRequest.getColorUI()
        );

        SmokeUserDetails userDetails =
            (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();

        smokeContextRequest.setContext(validatedLabel);
        smokeContextRequest.setColorUI(validatedColor);

        return smokeContextService.createSmokeContext(
            userId,
            smokeContextRequest
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SmokeContextDeletionResponse> deleteSmokeContext(
        Authentication authentication,
        @PathVariable String id
    ) {
        String validatedId = ValidationUtils.validateUUID(id);
        UUID contextId = UUID.fromString(validatedId);

        SmokeUserDetails userDetails =
            (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();

        try {
            smokeContextService.deleteSmokeContext(contextId, userId);
            log.info("Smoke context deleted: {}", contextId);
            return ResponseEntity.ok(
                new SmokeContextDeletionResponse(
                    true,
                    "Smoke context deleted successfully"
                )
            );
        } catch (AuthenticationError ex) {
            log.warn("Unauthorized delete attempt for context: {}", contextId);
            SmokeContextDeletionResponse response =
                new SmokeContextDeletionResponse(
                    false,
                    "Unauthorized: " + ex.getMessage()
                );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
