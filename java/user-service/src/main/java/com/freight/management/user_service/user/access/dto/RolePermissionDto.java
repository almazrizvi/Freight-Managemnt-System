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
public class RolePermissionDto {
    private String menuId;
    private String title;
    private String angularRoute;
    private String icon;
    private List<String> availableActions;
    private List<String> assignedActions;
}
