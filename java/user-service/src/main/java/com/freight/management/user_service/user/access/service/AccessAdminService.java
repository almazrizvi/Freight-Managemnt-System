package com.freight.management.user_service.user.access.service;

import com.freight.management.user_service.user.access.dto.AccessProfile;
import com.freight.management.user_service.user.access.dto.RolePermissionDto;
import com.freight.management.user_service.user.access.dto.RolePermissionMatrixDto;
import com.freight.management.user_service.user.access.dto.RolePermissionUpdateRequest;
import com.freight.management.user_service.user.access.dto.RoleSummaryDto;
import com.freight.management.user_service.user.access.dto.UserAccessDto;
import com.freight.management.user_service.user.access.model.AppAction;
import com.freight.management.user_service.user.access.model.AppRole;
import com.freight.management.user_service.user.access.model.RoleMenuAction;
import com.freight.management.user_service.user.access.model.UserRole;
import com.freight.management.user_service.user.access.repository.AppActionRepository;
import com.freight.management.user_service.user.access.repository.AppRoleRepository;
import com.freight.management.user_service.user.access.repository.RoleMenuActionRepository;
import com.freight.management.user_service.user.access.repository.UserRoleRepository;
import com.freight.management.user_service.user.model.Menu;
import com.freight.management.user_service.user.model.User;
import com.freight.management.user_service.user.repository.MenuRepository;
import com.freight.management.user_service.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AccessAdminService {

    private final AppRoleRepository appRoleRepository;
    private final AppActionRepository appActionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleMenuActionRepository roleMenuActionRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final AccessProfileService accessProfileService;

    public AccessAdminService(
            AppRoleRepository appRoleRepository,
            AppActionRepository appActionRepository,
            UserRoleRepository userRoleRepository,
            RoleMenuActionRepository roleMenuActionRepository,
            MenuRepository menuRepository,
            UserRepository userRepository,
            AccessProfileService accessProfileService
    ) {
        this.appRoleRepository = appRoleRepository;
        this.appActionRepository = appActionRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleMenuActionRepository = roleMenuActionRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.accessProfileService = accessProfileService;
    }

    @Transactional(readOnly = true)
    public List<RoleSummaryDto> getRoles() {
        return appRoleRepository.findByIsActiveTrueOrderByRoleNameAsc().stream()
                .map(role -> RoleSummaryDto.builder()
                        .roleCode(role.getRoleCode())
                        .roleName(role.getRoleName())
                        .description(role.getDescription())
                        .systemRole(role.getIsSystemRole())
                        .active(role.getIsActive())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public RolePermissionMatrixDto getRolePermissionMatrix(String roleCode) {
        AppRole role = requireActiveRole(roleCode);
        List<AppAction> actions = appActionRepository.findByIsActiveTrueOrderByDisplayNameAsc();
        List<Menu> menus = getPermissionMenus();
        List<RoleMenuAction> existingMappings = roleMenuActionRepository.findByRole_IdAndMenu_IsActiveTrueAndAction_IsActiveTrue(role.getId());

        Map<String, Set<String>> assignedActionMap = new LinkedHashMap<>();
        for (RoleMenuAction mapping : existingMappings) {
            assignedActionMap
                    .computeIfAbsent(mapping.getMenu().getMenuId(), ignored -> new LinkedHashSet<>())
                    .add(mapping.getAction().getActionCode());
        }

        List<String> availableActions = actions.stream()
                .map(AppAction::getActionCode)
                .toList();

        List<RolePermissionDto> permissions = menus.stream()
                .map(menu -> RolePermissionDto.builder()
                        .menuId(menu.getMenuId())
                        .title(menu.getTitle())
                        .angularRoute(menu.getAngularRoute())
                        .icon(menu.getIcon())
                        .availableActions(availableActions)
                        .assignedActions(assignedActionMap.getOrDefault(menu.getMenuId(), Set.of()).stream()
                                .sorted()
                                .toList())
                        .build())
                .toList();

        return RolePermissionMatrixDto.builder()
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .permissions(permissions)
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserAccessDto> getAllUserAccess() {
        return userRepository.findAll().stream()
                .filter(user -> user.getDeletedAt() == null)
                .sorted(Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::buildUserAccessDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserAccessDto getUserAccess(UUID userId) {
        User user = requireUser(userId);
        return buildUserAccessDto(user);
    }

    public UserAccessDto assignRoles(UUID userId, Collection<String> roleCodes) {
        User user = requireUser(userId);
        List<String> normalizedRoleCodes = roleCodes == null
                ? List.of()
                : roleCodes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .map(code -> code.toUpperCase(Locale.ROOT))
                .distinct()
                .toList();

        List<AppRole> roles = normalizedRoleCodes.isEmpty()
                ? List.of()
                : appRoleRepository.findByRoleCodeInAndIsActiveTrue(normalizedRoleCodes);

        if (roles.size() != normalizedRoleCodes.size()) {
            throw new IllegalArgumentException("One or more role codes are invalid");
        }

        userRoleRepository.deleteByUser_Id(userId);

        List<UserRole> newAssignments = new ArrayList<>();
        for (AppRole role : roles) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            newAssignments.add(userRole);
        }

        if (!newAssignments.isEmpty()) {
            userRoleRepository.saveAll(newAssignments);
        }

        return buildUserAccessDto(user);
    }

    public RolePermissionMatrixDto updateRolePermissions(String roleCode, RolePermissionUpdateRequest request) {
        AppRole role = requireActiveRole(roleCode);
        List<Menu> menus = getPermissionMenus();
        Map<String, Menu> menuById = menus.stream()
                .collect(LinkedHashMap::new, (map, menu) -> map.put(menu.getMenuId(), menu), Map::putAll);

        List<String> requestedActionCodes = request == null || request.getPermissions() == null
                ? List.of()
                : request.getPermissions().stream()
                .flatMap(permission -> permission.getActionCodes() == null ? List.<String>of().stream() : permission.getActionCodes().stream())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .map(code -> code.toUpperCase(Locale.ROOT))
                .distinct()
                .toList();

        Map<String, AppAction> actionByCode = requestedActionCodes.isEmpty()
                ? Map.of()
                : appActionRepository.findByActionCodeInAndIsActiveTrue(requestedActionCodes).stream()
                .collect(LinkedHashMap::new, (map, action) -> map.put(action.getActionCode(), action), Map::putAll);

        if (actionByCode.size() != requestedActionCodes.size()) {
            throw new IllegalArgumentException("One or more action codes are invalid");
        }

        roleMenuActionRepository.deleteByRole_Id(role.getId());

        List<RoleMenuAction> newMappings = new ArrayList<>();
        if (request != null && request.getPermissions() != null) {
            for (RolePermissionUpdateRequest.ModulePermissionUpdate permission : request.getPermissions()) {
                Menu menu = menuById.get(permission.getMenuId());
                if (menu == null) {
                    throw new IllegalArgumentException("Menu not found for permission update: " + permission.getMenuId());
                }
                if (permission.getActionCodes() == null) {
                    continue;
                }
                for (String actionCode : permission.getActionCodes()) {
                    AppAction action = actionByCode.get(actionCode.trim().toUpperCase(Locale.ROOT));
                    if (action == null) {
                        throw new IllegalArgumentException("Action not found for permission update: " + actionCode);
                    }

                    RoleMenuAction mapping = new RoleMenuAction();
                    mapping.setRole(role);
                    mapping.setMenu(menu);
                    mapping.setAction(action);
                    newMappings.add(mapping);
                }
            }
        }

        if (!newMappings.isEmpty()) {
            roleMenuActionRepository.saveAll(newMappings);
        }

        return getRolePermissionMatrix(roleCode);
    }

    private AppRole requireActiveRole(String roleCode) {
        return appRoleRepository.findByRoleCodeAndIsActiveTrue(roleCode == null ? null : roleCode.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleCode));
    }

    private User requireUser(UUID userId) {
        return userRepository.findById(userId)
                .filter(user -> user.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private UserAccessDto buildUserAccessDto(User user) {
        AccessProfile accessProfile = accessProfileService.buildAccessProfile(user);
        return UserAccessDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userType(user.getUserType())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .roleCodes(accessProfile.roles())
                .authorities(accessProfile.authorities())
                .menuIds(accessProfile.menuIds())
                .build();
    }

    private List<Menu> getPermissionMenus() {
        return menuRepository.findAllByOrderByDisplayOrder().stream()
                .filter(menu -> Boolean.TRUE.equals(menu.getIsActive()))
                .filter(menu -> menu.getAngularRoute() != null && !menu.getAngularRoute().isBlank())
                .toList();
    }
}
