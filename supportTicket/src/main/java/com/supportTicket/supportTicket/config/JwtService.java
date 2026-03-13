package com.supportTicket.supportTicket.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMillis;

    /**
     * Constructs the JWT service.
     * - The secret must be at least 32 characters for HS256 (256-bit).
     * - expirationMillis is the token validity duration in milliseconds.
     */
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMillis) {
        // Keys.hmacShaKeyFor will fail if secret is shorter than 32 bytes for HS256
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    /**
     * Generates a signed JWT with subject = username.
     */
    public String generateToken(String username) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username (subject) from the JWT.
     * Throws io.jsonwebtoken.JwtException for invalid/expired tokens.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Validates the token against a given UserDetails.
     * Checks username match and expiration.
     */
    public boolean isTokenValid(String token, org.springframework.security.core.userdetails.UserDetails user) {
        String username = extractUsername(token);
        return username != null && username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Returns true if the token is expired.
     */
    private boolean isTokenExpired(String token) {
        Date exp = extractAllClaims(token).getExpiration();
        return exp.before(new Date());
    }

    /**
     * Parses and verifies the token signature and returns all claims.
     * Throws io.jsonwebtoken.JwtException if the token is invalid.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}