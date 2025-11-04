package com.sassi.smokehabits.security;

import com.sassi.smokehabits.exception.InvalidTokenException;
import com.sassi.smokehabits.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint  authenticationEntryPoint;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService,  JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader(AUTHORIZATION);
            SecurityContext context = SecurityContextHolder.getContext();
            
            logger.info("JWT Filter processing request: {} {}", request.getMethod(), request.getRequestURI());

            if (authHeader != null && authHeader.startsWith("Bearer") && context.getAuthentication() == null) {
                if(authHeader.length() < 32){
                    throw new BadCredentialsException("Bad credentials");
                }
                String token = authHeader.substring(7);
                logger.info("JWT Filter: Processing Bearer token");
                
                try {
                    String type = jwtService.extractTokenType(token);
                    logger.info("JWT Filter: Token type: {}", type);
                    
                    if (!"access".equals(type)) {
                        throw new BadCredentialsException("Token is not an access token");
                    }

                    String username = jwtService.extractSubject(token);
                    logger.info("JWT Filter: Extracted username: {}", username);
                    
                    UserDetails user = userDetailsService.loadUserByUsername(username);
                    logger.info("JWT Filter: Loaded user details for: {}", username);

                    if (!jwtService.isTokenValid(token)) {
                        throw new CredentialsExpiredException("JWT token expired");
                    }

                    var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    logger.info("JWT Filter: Successfully authenticated user: {}", username);

                } catch (Exception e) {
                    logger.error("JWT Filter: Authentication error: {}", e.getMessage(), e);
                    authenticationEntryPoint.commence(request, response,
                            new org.springframework.security.core.AuthenticationException(e.getMessage(), e) {});
                    return; // stop filter chain
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("JWT Filter: Unexpected error for {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
            throw e;
        }
    }
}

