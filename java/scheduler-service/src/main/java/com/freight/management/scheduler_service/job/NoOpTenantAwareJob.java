package com.freight.management.scheduler_service.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@SchedulerJobComponent
public class NoOpTenantAwareJob extends TenantAwareQuartzJob {

    @Override
    protected void executeForTenant(JobExecutionContext context) throws JobExecutionException {
        log.info(
                "No-op scheduler job executed for tenant={} jobDefinitionId={}",
                context.getMergedJobDataMap().getString(TENANT_ID_KEY),
                context.getMergedJobDataMap().getString(JOB_DEFINITION_ID_KEY)
        );
    }
}
