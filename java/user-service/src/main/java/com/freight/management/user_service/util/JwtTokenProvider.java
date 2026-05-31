package com.freight.management.user_service.util;

import com.freight.management.core.jwt.JwtSettings;
import com.freight.management.core.jwt.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

	private final JwtTokenService jwtTokenService;

	public JwtTokenProvider(
			@Value("${jwt.secret:freight-system-secret-key-change-in-production-environment-please-rotate}") String jwtSecret,
			@Value("${jwt.expiration:3600000}") long jwtExpiration
	) {
		this.jwtTokenService = new JwtTokenService(new JwtSettings(jwtSecret, jwtExpiration));
	}

	public String generateToken(String userId, String email) {
		return jwtTokenService.generateToken(userId, email);
	}

	public boolean validateToken(String token) {
		return jwtTokenService.validateToken(token);
	}
}
