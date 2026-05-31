package com.freight.management.user_service.user.access.service;

import com.freight.management.user_service.user.access.dto.AccessProfile;
import com.freight.management.user_service.user.access.dto.RolePermissionMatrixDto;
import com.freight.management.user_service.user.access.dto.RolePermissionUpdateRequest;
import com.freight.management.user_service.user.access.dto.UserAccessDto;
import com.freight.management.user_service.user.access.model.AppAction;
import com.freight.management.user_service.user.access.model.AppRole;
import com.freight.management.user_service.user.access.model.RoleMenuAction;
import com.freight.management.user_service.user.access.repository.AppActionRepository;
import com.freight.management.user_service.user.access.repository.AppRoleRepository;
import com.freight.management.user_service.user.access.repository.RoleMenuActionRepository;
import com.freight.management.user_service.user.access.repository.UserRoleRepository;
import com.freight.management.user_service.user.model.Menu;
import com.freight.management.user_service.user.model.User;
import com.freight.management.user_service.user.repository.MenuRepository;
import com.freight.management.user_service.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessAdminServiceTest {

    @Mock
    private AppRoleRepository appRoleRepository;
    @Mock
    private AppActionRepository appActionRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private RoleMenuActionRepository roleMenuActionRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccessProfileService accessProfileService;

    @InjectMocks
    private AccessAdminService accessAdminService;

    @Test
    void shouldAssignRolesAndReturnUpdatedProfile() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("ops@freight.test");
        user.setFullName("Ops User");
        user.setUserType("INTERNAL");
        user.setIsActive(true);

        AppRole adminRole = new AppRole();
        adminRole.setId(UUID.randomUUID());
        adminRole.setRoleCode("ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(appRoleRepository.findByRoleCodeInAndIsActiveTrue(List.of("ADMIN"))).thenReturn(List.of(adminRole));
        when(accessProfileService.buildAccessProfile(user))
                .thenReturn(new AccessProfile(List.of("ADMIN"), List.of("shipments:view"), List.of("shipments")));

        UserAccessDto result = accessAdminService.assignRoles(userId, List.of("ADMIN"));

        verify(userRoleRepository).deleteByUser_Id(userId);
        verify(userRoleRepository).saveAll(anyCollection());
        assertEquals("ops@freight.test", result.getEmail());
        assertIterableEquals(List.of("ADMIN"), result.getRoleCodes());
    }

    @Test
    void shouldUpdateRolePermissionsFromRequest() {
        AppRole role = new AppRole();
        role.setId(UUID.randomUUID());
        role.setRoleCode("ADMIN");
        role.setRoleName("Administrator");

        Menu menu = new Menu();
        menu.setMenuId("shipments");
        menu.setTitle("Shipments");
        menu.setAngularRoute("/shipments");
        menu.setDisplayOrder(1);
        menu.setIsActive(true);

        AppAction viewAction = new AppAction();
        viewAction.setId(UUID.randomUUID());
        viewAction.setActionCode("VIEW");
        viewAction.setDisplayName("View");
        viewAction.setIsActive(true);

        RolePermissionUpdateRequest request = new RolePermissionUpdateRequest(
                List.of(new RolePermissionUpdateRequest.ModulePermissionUpdate("shipments", List.of("VIEW")))
        );

        when(appRoleRepository.findByRoleCodeAndIsActiveTrue("ADMIN")).thenReturn(Optional.of(role));
        when(menuRepository.findAllByOrderByDisplayOrder()).thenReturn(List.of(menu));
        when(appActionRepository.findByActionCodeInAndIsActiveTrue(List.of("VIEW"))).thenReturn(List.of(viewAction));
        when(appActionRepository.findByIsActiveTrueOrderByDisplayNameAsc()).thenReturn(List.of(viewAction));
        when(roleMenuActionRepository.findByRole_IdAndMenu_IsActiveTrueAndAction_IsActiveTrue(role.getId()))
                .thenReturn(List.of(new RoleMenuAction(null, role, menu, viewAction, null, null)));

        RolePermissionMatrixDto result = accessAdminService.updateRolePermissions("ADMIN", request);

        ArgumentCaptor<List<RoleMenuAction>> captor = ArgumentCaptor.forClass(List.class);
        verify(roleMenuActionRepository).saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals("ADMIN", result.getRoleCode());
        assertIterableEquals(List.of("VIEW"), result.getPermissions().get(0).getAssignedActions());
    }
}
