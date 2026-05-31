package com.freight.management.scheduler_service.repository;

import com.freight.management.scheduler_service.model.ScheduledJobDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduledJobDefinitionRepository extends JpaRepository<ScheduledJobDefinition, UUID> {

    List<ScheduledJobDefinition> findByTenantIdOrderByJobGroupAscJobNameAsc(String tenantId);

    Optional<ScheduledJobDefinition> findByIdAndTenantId(UUID id, String tenantId);

    boolean existsByTenantIdAndJobGroupAndJobName(String tenantId, String jobGroup, String jobName);

    boolean existsByTenantIdAndJobGroupAndJobNameAndIdNot(String tenantId, String jobGroup, String jobName, UUID id);

    List<ScheduledJobDefinition> findByTenantId(String tenantId);
}
