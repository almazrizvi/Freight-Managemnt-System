package com.freight.management.user_service.user.access.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccessDto {
    private UUID userId;
    private String email;
    private String fullName;
    private String userType;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<String> roleCodes;
    private List<String> authorities;
    private List<String> menuIds;
}
