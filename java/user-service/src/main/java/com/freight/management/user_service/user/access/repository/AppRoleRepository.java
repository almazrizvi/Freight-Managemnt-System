package com.freight.management.user_service.user.access.repository;

import com.freight.management.user_service.user.access.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, UUID> {

    Optional<AppRole> findByRoleCodeAndIsActiveTrue(String roleCode);

    List<AppRole> findByIsActiveTrueOrderByRoleNameAsc();

    List<AppRole> findByRoleCodeInAndIsActiveTrue(Collection<String> roleCodes);
}
