package com.dennis.auth.dtos;

import java.time.Instant;

public record LoginResponse(
        String accessToken,
        Instant expiresAt,
        String role) {
}
