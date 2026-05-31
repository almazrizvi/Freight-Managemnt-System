package com.freight.management.user_service.user.access.service;

import com.freight.management.user_service.user.access.dto.AccessProfile;
import com.freight.management.user_service.user.access.model.AppRole;
import com.freight.management.user_service.user.access.model.RoleMenuAction;
import com.freight.management.user_service.user.access.model.UserRole;
import com.freight.management.user_service.user.access.repository.AppRoleRepository;
import com.freight.management.user_service.user.access.repository.RoleMenuActionRepository;
import com.freight.management.user_service.user.access.repository.UserRoleRepository;
import com.freight.management.user_service.user.model.Menu;
import com.freight.management.user_service.user.model.User;
import com.freight.management.user_service.user.repository.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AccessProfileService {

    private final UserRoleRepository userRoleRepository;
    private final RoleMenuActionRepository roleMenuActionRepository;
    private final MenuRepository menuRepository;
    private final AppRoleRepository appRoleRepository;

    public AccessProfileService(
            UserRoleRepository userRoleRepository,
            RoleMenuActionRepository roleMenuActionRepository,
            MenuRepository menuRepository,
            AppRoleRepository appRoleRepository
    ) {
        this.userRoleRepository = userRoleRepository;
        this.roleMenuActionRepository = roleMenuActionRepository;
        this.menuRepository = menuRepository;
        this.appRoleRepository = appRoleRepository;
    }

    public AccessProfile buildAccessProfile(User user) {
        List<UserRole> userRoles = userRoleRepository.findByUser_IdAndRole_IsActiveTrue(user.getId());
        if (userRoles.isEmpty()) {
            return AccessProfile.empty();
        }

        List<String> roleCodes = userRoles.stream()
                .map(UserRole::getRole)
                .map(AppRole::getRoleCode)
                .distinct()
                .sorted()
                .toList();

        List<UUID> roleIds = userRoles.stream()
                .map(UserRole::getRole)
                .map(AppRole::getId)
                .distinct()
                .toList();

        List<RoleMenuAction> roleMenuActions =
                roleMenuActionRepository.findDistinctByRole_IdInAndMenu_IsActiveTrueAndAction_IsActiveTrue(roleIds);

        LinkedHashSet<String> authoritySet = new LinkedHashSet<>();
        LinkedHashSet<String> directMenuIds = new LinkedHashSet<>();

        for (RoleMenuAction roleMenuAction : roleMenuActions) {
            String menuId = roleMenuAction.getMenu().getMenuId();
            String actionCode = roleMenuAction.getAction().getActionCode().toLowerCase(Locale.ROOT);
            directMenuIds.add(menuId);
            authoritySet.add(menuId + ":" + actionCode);
        }

        List<Menu> orderedMenus = menuRepository.findAllByOrderByDisplayOrder();
        Map<String, Menu> menuById = new LinkedHashMap<>();
        for (Menu menu : orderedMenus) {
            menuById.put(menu.getMenuId(), menu);
        }

        Set<String> accessibleMenuIds = new LinkedHashSet<>(directMenuIds);
        for (String menuId : directMenuIds) {
            includeAncestors(menuId, menuById, accessibleMenuIds);
        }

        List<String> orderedAccessibleMenuIds = new ArrayList<>();
        for (Menu menu : orderedMenus) {
            if (accessibleMenuIds.contains(menu.getMenuId())) {
                orderedAccessibleMenuIds.add(menu.getMenuId());
            }
        }

        return new AccessProfile(roleCodes, authoritySet.stream().sorted().toList(), orderedAccessibleMenuIds);
    }

    public void assignDefaultRole(User user) {
        String roleCode = resolveDefaultRoleCode(user.getUserType());
        Optional<AppRole> appRole = appRoleRepository.findByRoleCodeAndIsActiveTrue(roleCode);
        if (appRole.isEmpty()) {
            return;
        }

        AppRole role = appRole.get();
        if (userRoleRepository.existsByUser_IdAndRole_Id(user.getId(), role.getId())) {
            return;
        }

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
    }

    private void includeAncestors(String menuId, Map<String, Menu> menuById, Collection<String> accessibleMenuIds) {
        Menu currentMenu = menuById.get(menuId);
        while (currentMenu != null && currentMenu.getParentId() != null && !currentMenu.getParentId().isBlank()) {
            accessibleMenuIds.add(currentMenu.getParentId());
            currentMenu = menuById.get(currentMenu.getParentId());
        }
    }

    private String resolveDefaultRoleCode(String userType) {
        if (userType == null || userType.isBlank()) {
            return "INTERNAL";
        }

        return switch (userType.trim().toUpperCase(Locale.ROOT)) {
            case "CUSTOMER" -> "CUSTOMER";
            case "DRIVER" -> "DRIVER";
            default -> "INTERNAL";
        };
    }
}
