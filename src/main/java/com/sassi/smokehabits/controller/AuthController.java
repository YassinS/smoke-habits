package com.sassi.smokehabits.controller;

import com.sassi.smokehabits.dto.security.request.LoginRequest;
import com.sassi.smokehabits.dto.security.request.RefreshTokenRequest;
import com.sassi.smokehabits.dto.security.request.RegisterRequest;
import com.sassi.smokehabits.dto.security.response.TokenResponse;
import com.sassi.smokehabits.entity.RefreshToken;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.security.service.RefreshTokenService;
import com.sassi.smokehabits.service.AuthService;
import com.sassi.smokehabits.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {
        TokenResponse token = authService.register(request);
        logger.info("Token: " + token);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(request.getRefreshToken());

        User user = refreshToken.getUser();

        String newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken, user);
        String newAccessToken = jwtService.generateToken(user.getEmail());
        TokenResponse tokenResponse = new TokenResponse(newAccessToken, newRefreshToken);
        return  ResponseEntity.ok(tokenResponse);




    }
}