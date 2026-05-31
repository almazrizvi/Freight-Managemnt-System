package com.freight.management.user_service.user.access.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleAssignmentRequest {
    private List<String> roleCodes;
}
