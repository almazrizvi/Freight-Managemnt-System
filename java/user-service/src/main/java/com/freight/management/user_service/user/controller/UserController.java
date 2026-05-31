package com.freight.management.user_service.user.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freight.management.user_service.user.model.User;
import com.freight.management.user_service.user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Create a new user
	 */
	@PostMapping
	public User createUser(@RequestBody User user) {
		return userService.createUser(user);
	}

	/**
	 * Get all active users
	 */
	@GetMapping
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	/**
	 * Get user by ID
	 */
	@GetMapping("/{id}")
	public User getUserById(@PathVariable UUID id) {
		return userService.getUserById(id);
	}

	/**
	 * Update user
	 */
	@PutMapping("/{id}")
	public User updateUser(@PathVariable UUID id, @RequestBody User user) {
		return userService.updateUser(id, user);
	}

	/**
	 * Delete user (soft delete)
	 */
	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable UUID id, @RequestParam UUID deletedBy) {
		userService.deleteUser(id, deletedBy);
	}

	/**
	 * Toggle user status (active/inactive)
	 */
	@PutMapping("/{id}/status")
	public User toggleUserStatus(@PathVariable UUID id, @RequestParam Boolean isActive) {
		return userService.toggleUserStatus(id, isActive);
	}

	/**
	 * Search users by name or email
	 */
	@GetMapping("/search")
	public List<User> searchUsers(@RequestParam String query) {
		return userService.searchUsers(query);
	}

	/**
	 * Get users by type
	 */
	@GetMapping("/type/{userType}")
	public List<User> getUsersByType(@PathVariable String userType) {
		return userService.getUsersByType(userType);
	}

	/**
	 * Bulk delete multiple users (soft delete)
	 */
	@PostMapping("/bulk-delete")
	public void bulkDeleteUsers(@RequestBody List<UUID> userIds, @RequestParam UUID deletedBy) {
		userService.bulkDeleteUsers(userIds, deletedBy);
	}

	/**
	 * Get user count
	 */
	@GetMapping("/count")
	public long getUserCount() {
		return userService.getUserCount();
	}
}
