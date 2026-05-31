package com.freight.management.scheduler_service.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@SchedulerJobComponent
public class TenantHeartbeatJob extends TenantAwareQuartzJob {

    @Override
    protected void executeForTenant(JobExecutionContext context) throws JobExecutionException {
        log.info(
                "Tenant heartbeat job fired for tenant={} payload={}",
                context.getMergedJobDataMap().getString(TENANT_ID_KEY),
                context.getMergedJobDataMap().getString(PAYLOAD_JSON_KEY)
        );
    }
}
