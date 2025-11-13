package com.sassi.smokehabits.service;

import com.sassi.smokehabits.dto.kafka.UserRegisteredEvent;
import com.sassi.smokehabits.dto.security.request.LoginRequest;
import com.sassi.smokehabits.dto.security.request.RegisterRequest;
import com.sassi.smokehabits.dto.security.response.TokenResponse;
import com.sassi.smokehabits.entity.RefreshToken;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.exception.AuthenticationError;
import com.sassi.smokehabits.exception.ValidationException;
import com.sassi.smokehabits.repository.UserRepository;
import com.sassi.smokehabits.security.service.RefreshTokenService;
import com.sassi.smokehabits.validation.ValidationUtils;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(
        AuthService.class
    );
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder =
        new BCryptPasswordEncoder();
    private final RefreshTokenService refreshTokenService;
    private final KafkaProducerService kafkaProducerService;

    public AuthService(
        UserRepository userRepository,
        JwtService jwtService,
        RefreshTokenService refreshTokenService,
        KafkaProducerService kafkaProducerService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.kafkaProducerService = kafkaProducerService;
    }

    public TokenResponse register(RegisterRequest request) {
        String validatedEmail = ValidationUtils.validateAndSanitizeEmail(
            request.getEmail()
        );
        String validatedPassword = ValidationUtils.validatePassword(
            request.getPassword()
        );
        ValidationUtils.validateConsent(request.isConsent());

        if (userRepository.findByEmail(validatedEmail).isPresent()) {
            log.warn(
                "Registration attempt with existing email: {}",
                validatedEmail
            );
            throw new ValidationException("Email is already registered");
        }

        String hashed = passwordEncoder.encode(validatedPassword);
        User user = new User(validatedEmail, hashed, true);
        userRepository.save(user);

        log.info("User registered successfully: {}", validatedEmail);

        // Publish event to Kafka for analytics service
        UserRegisteredEvent event = new UserRegisteredEvent(
            user.getId(),
            user.getEmail(),
            Instant.now()
        );
        kafkaProducerService.publishUserRegisteredEvent(event);

        String token = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(
            user
        );
        return new TokenResponse(token, refreshToken.getToken());
    }

    public TokenResponse login(LoginRequest request) {
        String validatedEmail = ValidationUtils.validateAndSanitizeEmail(
            request.getEmail()
        );

        User user = userRepository
            .findByEmail(validatedEmail)
            .orElseThrow(() -> {
                log.warn(
                    "Login attempt with non-existent email: {}",
                    validatedEmail
                );
                return new AuthenticationError("Invalid credentials");
            });

        if (
            !passwordEncoder.matches(
                request.getPassword(),
                user.getHashedPassword()
            )
        ) {
            log.warn("Failed login attempt for user: {}", validatedEmail);
            throw new AuthenticationError("Invalid credentials");
        }

        log.info("User logged in successfully: {}", validatedEmail);

        String token = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(
            user
        );
        return new TokenResponse(token, refreshToken.getToken());
    }
}
