package com.freight.management.core.security;

import com.freight.management.core.model.AuthenticatedUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public final class SecurityContextFactory {

    private SecurityContextFactory() {
    }

    public static Authentication createAuthentication(AuthenticatedUser user) {
        return UsernamePasswordAuthenticationToken.authenticated(
                user,
                null,
                user.authorities().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
    }
}
