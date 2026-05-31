package com.freight.management.user_service.user.access.repository;

import com.freight.management.user_service.user.access.model.RoleMenuAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoleMenuActionRepository extends JpaRepository<RoleMenuAction, UUID> {

    List<RoleMenuAction> findDistinctByRole_IdInAndMenu_IsActiveTrueAndAction_IsActiveTrue(Collection<UUID> roleIds);

    List<RoleMenuAction> findByRole_IdAndMenu_IsActiveTrueAndAction_IsActiveTrue(UUID roleId);

    void deleteByRole_Id(UUID roleId);
}
