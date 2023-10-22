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
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtService {
    @Value("${ignacio.app.jwtCookieName}")
    private String jwtCookie;
    @Value("${ignacio.app.jwtRefreshCookieName}")
    private String jwtRefreshCookie;
    @Value("${ignacio.app.jwtExpirationMs}")
    private String jwtExpirationMs;
    @Value("${ignacio.app.jwtSecret}")
    private String jwtSecret;

    private static final Logger logger= LoggerFactory.getLogger(JwtService.class);

    public ResponseCookie generateJwtCookie(UserDetails userDetails) {
        String jwt = generateJwtFromUsername(userDetails.getUsername());
        return generateCookie(jwtCookie, jwt, "/");
    }

    public ResponseCookie generateJwtCookie(User user) {
        String jwt = generateJwtFromUsername(user.getUsername());
        return generateCookie(jwtCookie, jwt, "/");
    }
    public ResponseCookie generateJwtRefreshCookie(String token) {
        return generateCookie(jwtRefreshCookie, token, "/auth/refreshtoken");
    }

    private ResponseCookie generateCookie(String name, String value, String path) {
        return ResponseCookie.from(name, value)
                .path(path)
                .httpOnly(true)
                .maxAge(24 * 60 * 60)
                .build();
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null).path("/").build();
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        return ResponseCookie.from(jwtRefreshCookie, null).path("/auth/refreshtoken").build();
    }

    public String getJwtFromCookie(HttpServletRequest request) {
        return getCookieValueByName(request, jwtCookie);
    }

    public String getJwtRefreshFromCookie(HttpServletRequest request) {
        return getCookieValueByName(request, jwtRefreshCookie);
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }

    private String generateJwtFromUsername(String username) {
        Instant now = Instant.now();
        Instant expiration = now.plus(Long.parseLong(jwtExpirationMs), ChronoUnit.MILLIS);
        return Jwts
                .builder()
                .signWith(key(), SignatureAlgorithm.HS256)
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
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
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
