package com.freight.management.scheduler_service.dto;

import com.freight.management.scheduler_service.model.CronMisfirePolicy;
import com.freight.management.scheduler_service.model.ScheduledJobDefinition;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class JobDefinitionResponse {
    UUID id;
    String tenantId;
    String jobName;
    String jobGroup;
    String description;
    String jobClassName;
    String cronExpression;
    String timeZone;
    Boolean active;
    Boolean paused;
    CronMisfirePolicy misfirePolicy;
    String payloadJson;
    LocalDateTime lastSyncedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static JobDefinitionResponse from(ScheduledJobDefinition definition) {
        return JobDefinitionResponse.builder()
                .id(definition.getId())
                .tenantId(definition.getTenantId())
                .jobName(definition.getJobName())
                .jobGroup(definition.getJobGroup())
                .description(definition.getDescription())
                .jobClassName(definition.getJobClassName())
                .cronExpression(definition.getCronExpression())
                .timeZone(definition.getTimeZone())
                .active(definition.getActive())
                .paused(definition.getPaused())
                .misfirePolicy(definition.getMisfirePolicy())
                .payloadJson(definition.getPayloadJson())
                .lastSyncedAt(definition.getLastSyncedAt())
                .createdAt(definition.getCreatedAt())
                .updatedAt(definition.getUpdatedAt())
                .build();
    }
}
