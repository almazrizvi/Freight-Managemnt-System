package com.freight.management.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthorityKeyTest {

    @Test
    void shouldFormatAuthorityUsingScreenAndAction() {
        AuthorityKey authorityKey = new AuthorityKey("shipment", "view");

        assertEquals("shipment:view", authorityKey.asAuthority());
    }

    @Test
    void shouldParseAuthorityUsingScreenAndAction() {
        AuthorityKey authorityKey = AuthorityKey.fromAuthority("shipment:update");

        assertEquals("shipment", authorityKey.screenCode());
        assertEquals("update", authorityKey.actionCode());
    }

    @Test
    void shouldRejectInvalidAuthorityFormat() {
        assertThrows(IllegalArgumentException.class, () -> AuthorityKey.fromAuthority("shipment"));
    }
}
