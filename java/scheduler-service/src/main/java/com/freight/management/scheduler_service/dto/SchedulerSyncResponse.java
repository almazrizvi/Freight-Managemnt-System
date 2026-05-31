package com.freight.management.scheduler_service.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SchedulerSyncResponse {
    String tenantId;
    int scheduledJobs;
    int removedJobs;
}
