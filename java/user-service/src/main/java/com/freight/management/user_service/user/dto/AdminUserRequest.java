package com.freight.management.user_service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserRequest {
    private String email;
    private String fullName;
    private String password;
    private String userType;
    private Boolean isActive;
    private List<String> roleCodes;
}
