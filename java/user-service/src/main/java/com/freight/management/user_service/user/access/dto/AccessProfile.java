package com.freight.management.user_service.user.access.dto;

import java.util.List;

public record AccessProfile(
        List<String> roles,
        List<String> authorities,
        List<String> menuIds
) {

    public AccessProfile {
        roles = roles == null ? List.of() : List.copyOf(roles);
        authorities = authorities == null ? List.of() : List.copyOf(authorities);
        menuIds = menuIds == null ? List.of() : List.copyOf(menuIds);
    }

    public static AccessProfile empty() {
        return new AccessProfile(List.of(), List.of(), List.of());
    }
}
