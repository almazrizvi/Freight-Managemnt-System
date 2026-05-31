package com.freight.management.user_service.user.access.service;

import com.freight.management.user_service.user.access.dto.AccessProfile;
import com.freight.management.user_service.user.access.model.AppAction;
import com.freight.management.user_service.user.access.model.AppRole;
import com.freight.management.user_service.user.access.model.RoleMenuAction;
import com.freight.management.user_service.user.access.model.UserRole;
import com.freight.management.user_service.user.access.repository.AppRoleRepository;
import com.freight.management.user_service.user.access.repository.RoleMenuActionRepository;
import com.freight.management.user_service.user.access.repository.UserRoleRepository;
import com.freight.management.user_service.user.model.Menu;
import com.freight.management.user_service.user.model.User;
import com.freight.management.user_service.user.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessProfileServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RoleMenuActionRepository roleMenuActionRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private AppRoleRepository appRoleRepository;

    @InjectMocks
    private AccessProfileService accessProfileService;

    @Test
    void shouldBuildAuthoritiesAndIncludeAncestorMenus() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        AppRole role = new AppRole();
        role.setId(roleId);
        role.setRoleCode("ADMIN");
        role.setIsActive(true);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        Menu admin = new Menu();
        admin.setMenuId("admin");
        admin.setDisplayOrder(1);

        Menu adminUsers = new Menu();
        adminUsers.setMenuId("admin_users");
        adminUsers.setParentId("admin");
        adminUsers.setDisplayOrder(2);
        adminUsers.setIsActive(true);

        AppAction viewAction = new AppAction();
        viewAction.setActionCode("VIEW");
        viewAction.setIsActive(true);

        RoleMenuAction roleMenuAction = new RoleMenuAction();
        roleMenuAction.setRole(role);
        roleMenuAction.setMenu(adminUsers);
        roleMenuAction.setAction(viewAction);

        when(userRoleRepository.findByUser_IdAndRole_IsActiveTrue(userId)).thenReturn(List.of(userRole));
        when(roleMenuActionRepository.findDistinctByRole_IdInAndMenu_IsActiveTrueAndAction_IsActiveTrue(List.of(roleId)))
                .thenReturn(List.of(roleMenuAction));
        when(menuRepository.findAllByOrderByDisplayOrder()).thenReturn(List.of(admin, adminUsers));

        AccessProfile accessProfile = accessProfileService.buildAccessProfile(user);

        assertEquals(List.of("ADMIN"), accessProfile.roles());
        assertIterableEquals(List.of("admin_users:view"), accessProfile.authorities());
        assertIterableEquals(List.of("admin", "admin_users"), accessProfile.menuIds());
    }

    @Test
    void shouldAssignDefaultCustomerRoleWhenPresent() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUserType("CUSTOMER");

        AppRole customerRole = new AppRole();
        customerRole.setId(roleId);
        customerRole.setRoleCode("CUSTOMER");

        when(appRoleRepository.findByRoleCodeAndIsActiveTrue("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(userRoleRepository.existsByUser_IdAndRole_Id(userId, roleId)).thenReturn(false);

        accessProfileService.assignDefaultRole(user);
    }
}
