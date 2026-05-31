package com.freight.management.user_service.service;

import com.freight.management.user_service.dto.LoginRequest;
import com.freight.management.user_service.dto.LoginResponse;
import com.freight.management.user_service.user.model.User;
import com.freight.management.user_service.user.repository.UserRepository;
import com.freight.management.user_service.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Value("${jwt.expiration:3600000}")
	private long jwtExpiration;

	/**
	 * Authenticate user and generate JWT token
	 */
	public LoginResponse login(LoginRequest loginRequest) throws Exception {
		// Find user by email
		Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

		if (userOptional.isEmpty()) {
			throw new Exception("User not found with email: " + loginRequest.getEmail());
		}

		User user = userOptional.get();

		// Check if user is active
		if (!user.getIsActive()) {
			throw new Exception("User account is inactive");
		}

		// Check if user is soft deleted
		if (user.getDeletedAt() != null) {
			throw new Exception("User account has been deleted");
		}

		// Verify password
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
			throw new Exception("Invalid password");
		}

		// Generate JWT token
		String token = jwtTokenProvider.generateToken(user.getId().toString(), user.getEmail());

		// Build response
		return LoginResponse.builder().token(token).userId(user.getId().toString()).email(user.getEmail())
				.fullName(user.getFullName()).userType(user.getUserType().toString()).expiresIn(jwtExpiration / 1000) // Convert
																														// to
																														// seconds
				.tokenType("Bearer").build();
	}

	/**
	 * Register new user
	 */
	public LoginResponse register(LoginRequest registerRequest) throws Exception {
		// Check if user already exists
		if (userRepository.existsByEmail(registerRequest.getEmail())) {
			throw new Exception("User already exists with email: " + registerRequest.getEmail());
		}

		// Create new user
		User newUser = new User();
		newUser.setEmail(registerRequest.getEmail());
		newUser.setFullName(registerRequest.getEmail()); // Use email as default name
		newUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
		newUser.setUserType("CUSTOMER"); // Default to CUSTOMER
		newUser.setIsActive(true);

		User savedUser = userRepository.save(newUser);

		// Generate JWT token
		String token = jwtTokenProvider.generateToken(savedUser.getId().toString(), savedUser.getEmail());

		// Build response
		return LoginResponse.builder().token(token).userId(savedUser.getId().toString()).email(savedUser.getEmail())
				.fullName(savedUser.getFullName()).userType(savedUser.getUserType()).expiresIn(jwtExpiration / 1000)
				.tokenType("Bearer").build();
	}

	/**
	 * Validate JWT token
	 */
	public boolean validateToken(String token) {
		return jwtTokenProvider.validateToken(token);
	}
}
