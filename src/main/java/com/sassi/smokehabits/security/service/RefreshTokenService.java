package com.sassi.smokehabits.security.service;

import com.sassi.smokehabits.entity.RefreshToken;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.exception.InvalidTokenException;
import com.sassi.smokehabits.exception.TokenExpiredException;
import com.sassi.smokehabits.repository.RefreshTokenRepository;
import com.sassi.smokehabits.service.JwtService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class RefreshTokenService {

    @Value("${token.secret}")
    private String secret;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    private Logger logger = LoggerFactory.getLogger(RefreshToken.class);

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }


    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken(
                user,
                jwtService.generateRefreshToken(user.getEmail())
        );
        logger.debug("Created refresh token for user {}", user.getEmail());
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken getRefreshToken(String token) {
        return refreshTokenRepository.findByTokenAndRevokedFalse(token).orElseThrow(() -> new InvalidTokenException("Refresh token invalid"));
    }

    private boolean isTokenFresh(String token) {
        RefreshToken refreshToken = getRefreshToken(token);

        Date expirationDate = jwtService.getExpirationDate(refreshToken.getToken());
        logger.debug("Checking if token expired for user {}", refreshToken.getUser().getEmail());
        return expirationDate.after(new Date());
    }

    @Transactional
    public String rotateRefreshToken(RefreshToken oldRefreshToken, User user) {
        if (!isTokenFresh(oldRefreshToken.getToken())) {
            logger.error("Invalid refresh token for user {}", user.getEmail());
            throw new TokenExpiredException("Refresh token expired");
        }
        if (!oldRefreshToken.getUser().getEmail().equals(user.getEmail())) {
            logger.error("Invalid refresh token for user {}", user.getEmail());
            throw new InvalidTokenException("Token does not belong to this user");
        }
        refreshTokenRepository.revokeAllByUser(user);
        RefreshToken newRefreshToken = new RefreshToken(
                user,
                jwtService.generateRefreshToken(user.getEmail())
        );
        logger.debug("Created refresh token for user {}", user.getEmail());

        oldRefreshToken.setRevoked(true);
        refreshTokenRepository.save(oldRefreshToken);
        logger.debug("Revoked stale refresh token for user {}", user.getEmail());

        return refreshTokenRepository.save(newRefreshToken).getToken();


    }

}
