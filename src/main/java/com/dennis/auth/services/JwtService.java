package com.dennis.auth.services;

import java.time.Instant;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dennis.auth.dtos.UserDto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

    private final SecretKey key;
    private final String jwtSecret;

    public JwtService(@Value("${jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDto user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(3600);

        return Jwts.builder()
                .setSubject(user.email())
                .claim("role", user.role())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logSecretFingerprint() {
        log.info("Auth JWT secret fingerprint: {}", fingerprint(jwtSecret));
    }

    private String fingerprint(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder firstBytes = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                firstBytes.append(String.format("%02x", hash[i]));
            }

            return "len=" + value.length() + ", sha256[0..7]=" + firstBytes;
        } catch (NoSuchAlgorithmException e) {
            return "sha256_unavailable";
        }
    }
}
