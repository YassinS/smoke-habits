package com.sassi.smokehabits.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/me")
public class MeController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMe(Authentication authentication) {
        // Spring injects Authentication automatically
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", authentication.getName());
        return ResponseEntity.ok(userInfo);
    }
}

