package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.dto.request.SmokeContextRequest;
import com.sassi.smokehabits.dto.response.SmokeContextResponse;
import com.sassi.smokehabits.entity.SmokeContext;
import com.sassi.smokehabits.repository.SmokeContextRepository;
import com.sassi.smokehabits.security.SmokeUserDetails;
import com.sassi.smokehabits.service.SmokeContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/context")
public class SmokeContextController {

    private static final Logger log = LoggerFactory.getLogger(SmokeContextController.class);
    private final SmokeContextService smokeContextService;
    private final SmokeContextRepository smokeContextRepository;

    public SmokeContextController(SmokeContextService smokeContextService, SmokeContextRepository smokeContextRepository) {
        this.smokeContextService = smokeContextService;
        this.smokeContextRepository = smokeContextRepository;
    }

    @GetMapping("/{id}")
    public SmokeContextResponse getSmokeContext(@PathVariable String id) {
        return smokeContextRepository.findById(UUID.fromString(id)).orElseThrow().toSmokeContextResponse();
    }

    @PostMapping("/{id}/edit")
    public SmokeContextResponse editSmokeContext(@PathVariable String id, @RequestBody SmokeContextRequest smokeContextRequest) {
        SmokeContext smokeContext = smokeContextRepository.findById(UUID.fromString(id)).orElseThrow();
        log.debug("Now editing {}", smokeContext.getId().toString());
        return smokeContextService.editSmokeContext(smokeContext, smokeContextRequest);
    }

    @GetMapping
    public List<SmokeContextResponse> findAll(Authentication authentication) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        return smokeContextService.findAllByUUID(userId);
    }

    @PostMapping
    public SmokeContextResponse create(Authentication authentication , @RequestBody SmokeContextRequest smokeContextRequest) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();

        return smokeContextService.createSmokeContext(userId, smokeContextRequest);
    }

}
