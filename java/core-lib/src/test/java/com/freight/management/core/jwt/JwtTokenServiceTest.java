package com.freight.management.core.jwt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenServiceTest {

    private static final String SECRET =
            "freight-system-secret-key-change-in-production-environment-please-rotate";

    @Test
    void shouldGenerateAndParseToken() {
        JwtTokenService jwtTokenService = new JwtTokenService(new JwtSettings(SECRET, 60_000));

        String token = jwtTokenService.generateToken("42", "ops@freight.test");

        assertTrue(jwtTokenService.validateToken(token));
        assertEquals("42", jwtTokenService.getUserIdFromToken(token));
        assertEquals("ops@freight.test", jwtTokenService.getEmailFromToken(token));
    }
}
