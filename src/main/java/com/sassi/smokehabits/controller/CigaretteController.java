package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.dto.CigaretteResponse;
import com.sassi.smokehabits.dto.LogCigaretteRequest;
import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.security.SmokeUserDetails;
import com.sassi.smokehabits.service.CigaretteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cigarettes")
public class CigaretteController {

    private final CigaretteService cigaretteService;

    public CigaretteController(CigaretteService cigaretteService) {
        this.cigaretteService = cigaretteService;
    }

    @PostMapping("/log")
    public ResponseEntity<CigaretteResponse> logCigarette(Authentication authentication,
                                                          @RequestBody LogCigaretteRequest logRequestBody) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        CigaretteEntry entry = cigaretteService.logCigarette(userId, logRequestBody.getCravingLevel() );
        CigaretteResponse response = new CigaretteResponse(entry.getId(),entry.getTimestamp());
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<CigaretteResponse>> getAll(Authentication authentication) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        List<CigaretteEntry> cigaretteEntries = cigaretteService.getAll(userId);
        List<CigaretteResponse> response = cigaretteEntries.stream()
                .map(entry -> new CigaretteResponse(
                        entry.getId(),
                        entry.getTimestamp())
                ).toList();
        return ResponseEntity.ok(response);
    }
}

