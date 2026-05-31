package com.freight.management.user_service.user.access.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleSummaryDto {
    private String roleCode;
    private String roleName;
    private String description;
    private Boolean systemRole;
    private Boolean active;
}
