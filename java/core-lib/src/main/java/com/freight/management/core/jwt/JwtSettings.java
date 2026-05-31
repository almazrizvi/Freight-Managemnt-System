package com.freight.management.core.jwt;

import java.util.Objects;

public record JwtSettings(String secret, long expirationMillis) {

    public JwtSettings {
        Objects.requireNonNull(secret, "secret must not be null");
        if (secret.isBlank()) {
            throw new IllegalArgumentException("secret must not be blank");
        }
        if (expirationMillis <= 0) {
            throw new IllegalArgumentException("expirationMillis must be greater than zero");
        }
    }
}
