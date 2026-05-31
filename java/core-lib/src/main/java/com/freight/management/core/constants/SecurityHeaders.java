package com.freight.management.core.constants;

public final class SecurityHeaders {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TENANT_ID = "X-Tenant-Id";
    public static final String USER_ID = "X-User-Id";
    public static final String USER_EMAIL = "X-User-Email";
    public static final String JWT_TOKEN = "X-JWT-Token";
    public static final String USER_AUTHORITIES = "X-User-Authorities";

    private SecurityHeaders() {
    }
}
