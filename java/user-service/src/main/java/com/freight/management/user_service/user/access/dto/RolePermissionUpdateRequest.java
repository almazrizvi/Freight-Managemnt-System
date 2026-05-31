package com.freight.management.user_service.user.access.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionUpdateRequest {

    private List<ModulePermissionUpdate> permissions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModulePermissionUpdate {
        private String menuId;
        private List<String> actionCodes;
    }
}
