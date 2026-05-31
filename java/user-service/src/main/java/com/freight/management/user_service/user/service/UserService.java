package com.freight.management.user_service.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.freight.management.user_service.user.model.User;
import com.freight.management.user_service.user.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Create a new user with email validation
	 */
	public User createUser(User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("Email already exists");
		}
		user.setIsActive(true);
		user.setUserType("INTERNAL");
		return userRepository.save(user);
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
	public User updateUser(UUID id, User user) {
		Optional<User> existingUser = userRepository.findById(id);
		if (existingUser.isPresent()) {
			User userToUpdate = existingUser.get();

			// Check if email is being changed and if it already exists
			if (user.getEmail() != null && !user.getEmail().equals(userToUpdate.getEmail())) {
				if (userRepository.existsByEmail(user.getEmail())) {
					throw new IllegalArgumentException("Email already exists");
				}
				userToUpdate.setEmail(user.getEmail());
			}

			if (user.getFullName() != null) {
				userToUpdate.setFullName(user.getFullName());
			}
			if (user.getPasswordHash() != null) {
				userToUpdate.setPasswordHash(user.getPasswordHash());
			}
			if (user.getUserType() != null) {
				userToUpdate.setUserType(user.getUserType());
			}
			if (user.getIsActive() != null) {
				userToUpdate.setIsActive(user.getIsActive());
			}

			return userRepository.save(userToUpdate);
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
}
