package com.sassi.smokehabits.service;

import com.sassi.smokehabits.dto.security.request.LoginRequest;
import com.sassi.smokehabits.dto.security.request.RegisterRequest;
import com.sassi.smokehabits.dto.security.response.TokenResponse;
import com.sassi.smokehabits.entity.RefreshToken;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.exception.AuthenticationError;
import com.sassi.smokehabits.repository.UserRepository;
import com.sassi.smokehabits.security.service.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, JwtService jwtService,  RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public TokenResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        String hashed = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getEmail(), hashed);
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new TokenResponse(token, refreshToken.getToken());
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getHashedPassword())) {
            throw new AuthenticationError("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new TokenResponse(token, refreshToken.getToken());
    }
}
