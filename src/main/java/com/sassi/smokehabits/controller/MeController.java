package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.security.SmokeUserDetails;
import com.sassi.smokehabits.service.UserService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class MeController {

    private static final Logger log = LoggerFactory.getLogger(
        MeController.class
    );
    private final UserService userService;

    public MeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMe(
        Authentication authentication
    ) {
        SmokeUserDetails userDetails =
            (SmokeUserDetails) authentication.getPrincipal();
        log.debug("Retrieved user info for: {}", userDetails.getUserId());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", authentication.getName());
        return ResponseEntity.ok(userInfo);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteMe(
        Authentication authentication
    ) {
        SmokeUserDetails userDetails =
            (SmokeUserDetails) authentication.getPrincipal();
        log.warn(
            "User account deletion requested for: {}",
            userDetails.getUserId()
        );

        userService.deleteUser(userDetails.getUserId());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User account deleted successfully");
        return ResponseEntity.ok(response);
    }
}
