package com.freight.management.core.context;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TenantContextTest {

    @Test
    void shouldStoreAndClearTenantId() {
        TenantContext.setTenantId("tenant-a");

        assertEquals("tenant-a", TenantContext.getTenantId().orElseThrow());

        TenantContext.clear();

        assertTrue(TenantContext.getTenantId().isEmpty());
    }
}
