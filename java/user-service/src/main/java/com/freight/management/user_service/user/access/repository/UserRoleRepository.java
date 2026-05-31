package com.freight.management.user_service.user.access.repository;

import com.freight.management.user_service.user.access.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    List<UserRole> findByUser_IdAndRole_IsActiveTrue(UUID userId);

    List<UserRole> findByUser_IdInAndRole_IsActiveTrue(Collection<UUID> userIds);

    boolean existsByUser_IdAndRole_Id(UUID userId, UUID roleId);

    void deleteByUser_Id(UUID userId);
}
