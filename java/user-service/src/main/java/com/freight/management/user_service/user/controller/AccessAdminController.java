package com.freight.management.user_service.user.controller;

import com.freight.management.user_service.user.access.dto.RolePermissionMatrixDto;
import com.freight.management.user_service.user.access.dto.RolePermissionUpdateRequest;
import com.freight.management.user_service.user.access.dto.RoleSummaryDto;
import com.freight.management.user_service.user.access.dto.UserAccessDto;
import com.freight.management.user_service.user.access.dto.UserRoleAssignmentRequest;
import com.freight.management.user_service.user.access.service.AccessAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/access")
public class AccessAdminController {

    private final AccessAdminService accessAdminService;

    public AccessAdminController(AccessAdminService accessAdminService) {
        this.accessAdminService = accessAdminService;
    }

    @GetMapping("/roles")
    public List<RoleSummaryDto> getRoles() {
        return accessAdminService.getRoles();
    }

    @GetMapping("/permissions/{roleCode}")
    public RolePermissionMatrixDto getRolePermissionMatrix(@PathVariable String roleCode) {
        return accessAdminService.getRolePermissionMatrix(roleCode);
    }

    @PutMapping("/permissions/{roleCode}")
    public RolePermissionMatrixDto updateRolePermissionMatrix(
            @PathVariable String roleCode,
            @RequestBody RolePermissionUpdateRequest request
    ) {
        return accessAdminService.updateRolePermissions(roleCode, request);
    }

    @GetMapping("/users")
    public List<UserAccessDto> getAllUserAccess() {
        return accessAdminService.getAllUserAccess();
    }

    @GetMapping("/users/{userId}")
    public UserAccessDto getUserAccess(@PathVariable UUID userId) {
        return accessAdminService.getUserAccess(userId);
    }

    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<UserAccessDto> assignRoles(
            @PathVariable UUID userId,
            @RequestBody UserRoleAssignmentRequest request
    ) {
        return ResponseEntity.ok(accessAdminService.assignRoles(userId, request.getRoleCodes()));
    }
}
