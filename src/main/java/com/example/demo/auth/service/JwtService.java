package com.example.demo.auth.service;

import com.example.demo.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtService {
    @Value("${ignacio.app.jwtSecret}")
    private String secretKey;
    @Value("${ignacio.app.jwtExpirationMs}")
    private Long jwtExpirationMs;
    @Value("${ignacio.app.jwtRefreshExpirationMs}")
    private Long refreshExpirationMs;

    private static final Logger logger= LoggerFactory.getLogger(JwtService.class);

    public String generateJwtToken(UserDetailsImpl userPrincipal) {
        return generateJwtFromUsername(userPrincipal.getUsername());
    }

    public String generateJwtFromUsername(String username) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtExpirationMs, ChronoUnit.MILLIS);
        return Jwts
                .builder()
                .signWith(key(), SignatureAlgorithm.HS384)
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .compact();
    }

    public String getUsernameFromJwt(String jwt){
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(jwt)
                .getBody().getSubject();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public boolean isJwtValid(String jwt) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(jwt);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
