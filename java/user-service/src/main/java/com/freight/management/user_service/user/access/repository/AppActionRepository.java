package com.freight.management.user_service.user.access.repository;

import com.freight.management.user_service.user.access.model.AppAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppActionRepository extends JpaRepository<AppAction, UUID> {

    Optional<AppAction> findByActionCodeAndIsActiveTrue(String actionCode);

    List<AppAction> findByIsActiveTrueOrderByDisplayNameAsc();

    List<AppAction> findByActionCodeInAndIsActiveTrue(Collection<String> actionCodes);
}
