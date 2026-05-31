package com.freight.management.core.model;

import java.util.Set;

public record AuthenticatedUser(
        String userId,
        String email,
        String tenantId,
        Set<String> authorities
) {

    public AuthenticatedUser {
        authorities = authorities == null ? Set.of() : Set.copyOf(authorities);
    }
}
