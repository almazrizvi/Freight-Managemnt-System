package com.freight.management.core.model;

import java.util.Objects;

public record AuthorityKey(String screenCode, String actionCode) {

    public AuthorityKey {
        Objects.requireNonNull(screenCode, "screenCode must not be null");
        Objects.requireNonNull(actionCode, "actionCode must not be null");
    }

    public String asAuthority() {
        return screenCode + ":" + actionCode;
    }

    public static AuthorityKey fromAuthority(String authority) {
        String[] parts = authority.split(":", 2);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalArgumentException("Authority must follow screen:action format");
        }

        return new AuthorityKey(parts[0], parts[1]);
    }
}
