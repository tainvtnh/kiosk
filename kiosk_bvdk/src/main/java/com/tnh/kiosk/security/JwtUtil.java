package com.tnh.kiosk.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String secret;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;
    private Key key;

    public JwtUtil(Environment env) {
        this.secret = env.getProperty("app.jwt-secret", "default-secret-please-change");
        this.accessExpirationMs = Long.parseLong(env.getProperty("app.jwt-expiration-ms", "3600000"));        // 1h
        this.refreshExpirationMs = Long.parseLong(env.getProperty("app.jwt-refresh-expiration-ms", "86400000")); // 1d
    }

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Sinh Access Token
    public String generateAccessToken(String username) {
        return generateToken(username, accessExpirationMs);
    }

    // Sinh Refresh Token
    public String generateRefreshToken(String username) {
        return generateToken(username, refreshExpirationMs);
    }

    // Sinh token chung
    private String generateToken(String username, long durationMs) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + durationMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    // Lấy username từ token
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // Lấy thời điểm hết hạn
    public Date getExpirationDate(String token) {
        return parseClaims(token).getExpiration();
    }

    // Kiểm tra token hợp lệ
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new JwtException("Invalid JWT token", ex);
            //return false;
        }
    }

    // Phân tích claim
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Getter
    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }
}
