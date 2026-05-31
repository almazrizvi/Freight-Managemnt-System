package com.freight.management.user_service.user.controller;

import com.freight.management.user_service.dto.LoginRequest;
import com.freight.management.user_service.dto.LoginResponse;
import com.freight.management.user_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

	@Autowired
	private AuthService authService;

	/**
	 * User login endpoint POST /users/login Body: { "email": "user@example.com",
	 * "password": "password123" } Returns: JWT token and user details
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		try {
			if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()) {
				return ResponseEntity.badRequest().body("Email is required");
			}
			if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
				return ResponseEntity.badRequest().body("Password is required");
			}

			LoginResponse response = authService.login(loginRequest);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
		}
	}

	/**
	 * User registration endpoint POST /users/register Body: { "email":
	 * "newuser@example.com", "password": "password123" } Returns: JWT token and new
	 * user details
	 */
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody LoginRequest registerRequest) {
		try {
			if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
				return ResponseEntity.badRequest().body("Email is required");
			}
			if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
				return ResponseEntity.badRequest().body("Password is required");
			}

			LoginResponse response = authService.register(registerRequest);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
		}
	}

	/**
	 * Validate token endpoint GET /users/validate-token Header: Authorization:
	 * Bearer {token} Returns: true/false
	 */
	@GetMapping("/validate-token")
	public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String bearerToken) {
		try {
			if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
				return ResponseEntity.badRequest().body("Invalid token format");
			}

			String token = bearerToken.substring(7);
			boolean isValid = authService.validateToken(token);
			return ResponseEntity.ok(new TokenValidationResponse(isValid));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token validation failed: " + e.getMessage());
		}
	}

	/**
	 * Simple response class for token validation
	 */
	@lombok.Data
	@lombok.AllArgsConstructor
	public static class TokenValidationResponse {
		private boolean valid;
	}
}
