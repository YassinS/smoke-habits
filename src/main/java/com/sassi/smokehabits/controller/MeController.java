package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.repository.UserRepository;
import com.sassi.smokehabits.security.SmokeUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/me")
public class MeController {
    private final UserRepository userRepository;

    public MeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMe(Authentication authentication) {
        // Spring injects Authentication automatically
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", authentication.getName());
        return ResponseEntity.ok(userInfo);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteMe(Authentication authentication) {
        SmokeUserDetails userDetails = (SmokeUserDetails) authentication.getPrincipal();
        User user = userRepository.getUserById(userDetails.getUserId());
        userRepository.delete(user);
        return ResponseEntity.ok(new HashMap<>());
    }
}

