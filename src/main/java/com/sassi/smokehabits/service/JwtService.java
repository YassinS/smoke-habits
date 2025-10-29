package com.sassi.smokehabits.service;

import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.security.SmokeUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${token.secret}")
    private String secret;
    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private SecretKey generateSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }


    public boolean isTokenValid(String jwtTokenHeader) {
        return new Date().before(extractExpiration(jwtTokenHeader));
    }

    private Date extractExpiration(String jwtTokenHeader) {
        return extractClaims(jwtTokenHeader).getExpiration();
    }

    public String generateToken(String userEmail) {
        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(userEmail)
                .issuedAt(getIssuedAt())
                .expiration(getExpiration())
                .signWith(generateSigningKey())
                .compact();
    }


    private Date getExpiration() {
        return new Date(System.currentTimeMillis() +(1000 * 24*24));
    }

    private Date getIssuedAt() {
        return new Date(System.currentTimeMillis());
    }
}

