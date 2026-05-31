package com.freight.management.user_service.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.freight.management.user_service.user.access.service.AccessAdminService;
import com.freight.management.user_service.user.access.service.AccessProfileService;
import com.freight.management.user_service.user.dto.AdminUserRequest;
import com.freight.management.user_service.user.model.User;
import com.freight.management.user_service.user.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final AccessProfileService accessProfileService;
	private final AccessAdminService accessAdminService;

	public UserService(
			UserRepository userRepository,
			BCryptPasswordEncoder passwordEncoder,
			AccessProfileService accessProfileService,
			AccessAdminService accessAdminService
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.accessProfileService = accessProfileService;
		this.accessAdminService = accessAdminService;
	}

	/**
	 * Create a new user with email validation
	 */
	public User createUser(AdminUserRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email already exists");
		}
		User user = new User();
		user.setEmail(request.getEmail());
		user.setFullName(request.getFullName());
		user.setPasswordHash(passwordEncoder.encode(requirePassword(request.getPassword())));
		user.setUserType(request.getUserType());
		user.setIsActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive());

		User savedUser = userRepository.save(user);

		if (request.getRoleCodes() != null && !request.getRoleCodes().isEmpty()) {
			accessAdminService.assignRoles(savedUser.getId(), request.getRoleCodes());
		} else {
			accessProfileService.assignDefaultRole(savedUser);
		}

		return savedUser;
	}

	/**
	 * Get all active users (excluding soft-deleted)
	 */
	public List<User> getAllUsers() {
		return userRepository.findAll().stream().filter(user -> user.getDeletedAt() == null)
				.collect(Collectors.toList());
	}

	/**
	 * Get user by ID (returns null if soft-deleted)
	 */
	public User getUserById(UUID id) {
		Optional<User> user = userRepository.findById(id);
		if (user.isPresent() && user.get().getDeletedAt() == null) {
			return user.get();
		}
		return null;
	}

	/**
	 * Update user with email uniqueness validation
	 */
	public User updateUser(UUID id, AdminUserRequest request) {
		Optional<User> existingUser = userRepository.findById(id);
		if (existingUser.isPresent()) {
			User userToUpdate = existingUser.get();

			// Check if email is being changed and if it already exists
			if (request.getEmail() != null && !request.getEmail().equals(userToUpdate.getEmail())) {
				if (userRepository.existsByEmail(request.getEmail())) {
					throw new IllegalArgumentException("Email already exists");
				}
				userToUpdate.setEmail(request.getEmail());
			}

			if (request.getFullName() != null) {
				userToUpdate.setFullName(request.getFullName());
			}
			if (request.getPassword() != null && !request.getPassword().isBlank()) {
				userToUpdate.setPasswordHash(passwordEncoder.encode(request.getPassword()));
			}
			if (request.getUserType() != null) {
				userToUpdate.setUserType(request.getUserType());
			}
			if (request.getIsActive() != null) {
				userToUpdate.setIsActive(request.getIsActive());
			}

			User updatedUser = userRepository.save(userToUpdate);
			if (request.getRoleCodes() != null) {
				accessAdminService.assignRoles(updatedUser.getId(), request.getRoleCodes());
			}
			return updatedUser;
		}
		return null;
	}

	/**
	 * Soft delete user with audit trail
	 */
	public void deleteUser(UUID id, UUID deletedBy) {
		Optional<User> user = userRepository.findById(id);
		if (user.isPresent()) {
			User userToDelete = user.get();
			userToDelete.setDeletedAt(LocalDateTime.now());
			userToDelete.setDeletedBy(deletedBy);
			userRepository.save(userToDelete);
		}
	}

	/**
	 * Toggle user status (active/inactive)
	 */
	public User toggleUserStatus(UUID id, Boolean isActive) {
		Optional<User> user = userRepository.findById(id);
		if (user.isPresent()) {
			User userToUpdate = user.get();
			userToUpdate.setIsActive(isActive);
			return userRepository.save(userToUpdate);
		}
		return null;
	}

	/**
	 * Search users by full name or email (case-insensitive, excludes soft-deleted)
	 */
	public List<User> searchUsers(String query) {
		return userRepository.findAll().stream().filter(user -> user.getDeletedAt() == null)
				.filter(user -> user.getFullName().toLowerCase().contains(query.toLowerCase())
						|| user.getEmail().toLowerCase().contains(query.toLowerCase()))
				.collect(Collectors.toList());
	}

	/**
	 * Get users by type (excludes soft-deleted)
	 */
	public List<User> getUsersByType(String userType) {
		return userRepository.findAll().stream().filter(user -> user.getDeletedAt() == null)
				.filter(user -> user.getUserType().equals(userType)).collect(Collectors.toList());
	}

	/**
	 * Soft delete multiple users with audit trail
	 */
	public void bulkDeleteUsers(List<UUID> userIds, UUID deletedBy) {
		for (UUID id : userIds) {
			deleteUser(id, deletedBy);
		}
	}

	/**
	 * Get count of active users (excluding soft-deleted)
	 */
	public long getUserCount() {
		return userRepository.findAll().stream().filter(user -> user.getDeletedAt() == null).count();
	}

	private String requirePassword(String password) {
		if (password == null || password.isBlank()) {
			throw new IllegalArgumentException("Password is required");
		}
		return password;
	}
}
