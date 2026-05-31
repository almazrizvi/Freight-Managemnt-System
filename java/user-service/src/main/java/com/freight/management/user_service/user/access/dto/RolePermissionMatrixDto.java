package com.freight.management.user_service.user.access.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionMatrixDto {
    private String roleCode;
    private String roleName;
    private String description;
    private List<RolePermissionDto> permissions;
}
