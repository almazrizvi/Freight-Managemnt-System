package com.freight.management.user_service.user.service;

import com.freight.management.user_service.user.access.service.AccessAdminService;
import com.freight.management.user_service.user.access.service.AccessProfileService;
import com.freight.management.user_service.user.dto.AdminUserRequest;
import com.freight.management.user_service.user.model.User;
import com.freight.management.user_service.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private AccessProfileService accessProfileService;
    @Mock
    private AccessAdminService accessAdminService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserAndAssignRequestedRoles() {
        AdminUserRequest request = new AdminUserRequest(
                "ops@freight.test",
                "Ops User",
                "secret",
                "INTERNAL",
                true,
                List.of("ADMIN")
        );

        User savedUser = new User();
        savedUser.setId(java.util.UUID.randomUUID());
        savedUser.setEmail(request.getEmail());
        savedUser.setFullName(request.getFullName());
        savedUser.setUserType(request.getUserType());
        savedUser.setIsActive(true);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed-secret");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("hashed-secret", captor.getValue().getPasswordHash());
        assertEquals("INTERNAL", result.getUserType());
        verify(accessAdminService).assignRoles(savedUser.getId(), List.of("ADMIN"));
        verify(accessProfileService, never()).assignDefaultRole(any(User.class));
    }

    @Test
    void shouldRequirePasswordOnCreate() {
        AdminUserRequest request = new AdminUserRequest(
                "ops@freight.test",
                "Ops User",
                " ",
                "INTERNAL",
                true,
                null
        );

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
        assertTrue(exception.getMessage().contains("Password is required"));
    }
}
