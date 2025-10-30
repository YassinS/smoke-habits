package com.sassi.smokehabits.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${token.secret}")
    private String secret;
    private static final long REFRESH_EXPIRATION_MILLIS = 7 * 24 * 60 * 60 * 1000; // 7 days

    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractTokenType(String token) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parseSignedClaims(token).getHeader().getType();


    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private SecretKey generateSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Date getExpirationDate(String token) {
        return extractClaims(token).getExpiration();
    }

    public boolean isTokenValid(String jwtTokenHeader) {
        return new Date().before(extractExpiration(jwtTokenHeader));
    }

    private Date extractExpiration(String jwtTokenHeader) {
        return extractClaims(jwtTokenHeader).getExpiration();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_EXPIRATION_MILLIS);

        return Jwts.builder()
                .header()
                .type("refresh")
                .and()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(generateSigningKey())
                .compact();
    }

    public String generateToken(String userEmail) {
        return Jwts.builder()
                .header()
                .type("access")
                .and()
                .subject(userEmail)
                .issuedAt(getIssuedAt())
                .expiration(getExpiration())
                .signWith(generateSigningKey())
                .compact();
    }

    private Date getExpiration() {
        return new Date(System.currentTimeMillis() +(1000 * 60 * 15));
    }

    private Date getIssuedAt() {
        return new Date(System.currentTimeMillis());
    }
}

