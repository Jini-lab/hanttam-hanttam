package com.hanttamhanttam.common.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(Long userId) {
        return createToken(
                userId,
                accessTokenExpiration,
                "ACCESS");
    }

    public String createRefreshToken(Long userId) {
        return createToken(
                userId,
                refreshTokenExpiration,
                "REFRESH");
    }

    private String createToken(
            Long userId,
            Long expiration,
            String type
    ) {
        Date now = new Date();
        Date expiresAt =
                new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }

    public Long getUserId(String token) {
        String subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return Long.valueOf(subject);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return hasTokenType(token, "ACCESS");
    }

    public boolean isRefreshToken(String token) {
        return hasTokenType(token, "REFRESH");
    }

    private boolean hasTokenType(
            String token,
            String expectedType
    ) {
        try {
            String type = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("type", String.class);

            return expectedType.equals(type);

        } catch (Exception e) {
            return false;
        }
    }

    public LocalDateTime getRefreshTokenExpiresAt() {
        return LocalDateTime.now()
                .plusNanos(refreshTokenExpiration * 1_000_000);
    }

}
