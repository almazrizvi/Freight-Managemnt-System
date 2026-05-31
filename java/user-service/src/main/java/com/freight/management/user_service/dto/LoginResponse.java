package com.freight.management.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
	private String token;
	private String userId;
	private String email;
	private String fullName;
	private String userType;
	private Long expiresIn;
	@Builder.Default
	private String tokenType = "Bearer";
	@Builder.Default
	private List<String> roles = List.of();
	@Builder.Default
	private List<String> authorities = List.of();
	@Builder.Default
	private List<String> menuIds = List.of();
}
