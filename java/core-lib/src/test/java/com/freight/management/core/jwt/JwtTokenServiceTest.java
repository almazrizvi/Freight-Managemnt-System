package com.freight.management.core.jwt;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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

    @Test
    void shouldIncludeAdditionalClaims() {
        JwtTokenService jwtTokenService = new JwtTokenService(new JwtSettings(SECRET, 60_000));

        String token = jwtTokenService.generateToken(
                "42",
                "ops@freight.test",
                Map.of(JwtClaimNames.ROLES, List.of("ADMIN"))
        );

        assertEquals(List.of("ADMIN"), jwtTokenService.getClaimsFromToken(token).get(JwtClaimNames.ROLES, List.class));
    }
}
