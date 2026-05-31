package com.freight.management.user_service.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret:freight-system-secret-key-change-in-production-environment}")
	private String jwtSecret;

	@Value("${jwt.expiration:3600000}")
	private long jwtExpiration;

	/**
	 * Generate JWT token
	 */
	public String generateToken(String userId, String email) {
		SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

		return Jwts.builder().subject(userId).claim("email", email).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(key, SignatureAlgorithm.HS512).compact();
	}

	/**
	 * Validate JWT token
	 */
	public boolean validateToken(String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (MalformedJwtException ex) {
			System.err.println("Invalid JWT token: " + ex.getMessage());
		} catch (ExpiredJwtException ex) {
			System.err.println("Expired JWT token: " + ex.getMessage());
		} catch (UnsupportedJwtException ex) {
			System.err.println("Unsupported JWT token: " + ex.getMessage());
		} catch (IllegalArgumentException ex) {
			System.err.println("JWT claims string is empty: " + ex.getMessage());
		}
		return false;
	}

	/**
	 * Get JWT claims
	 */
	public Claims getClaimsFromToken(String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
		} catch (Exception e) {
			System.err.println("Error getting claims from token: " + e.getMessage());
			throw new RuntimeException("Invalid token");
		}
	}

	/**
	 * Get User ID from token
	 */
	public String getUserIdFromToken(String token) {
		return getClaimsFromToken(token).getSubject();
	}

	/**
	 * Get Email from token
	 */
	public String getEmailFromToken(String token) {
		return getClaimsFromToken(token).get("email", String.class);
	}

	/**
	 * Check if token is expired
	 */
	public boolean isTokenExpired(String token) {
		try {
			Date expiration = getClaimsFromToken(token).getExpiration();
			return expiration.before(new Date());
		} catch (Exception e) {
			return true;
		}
	}
}
