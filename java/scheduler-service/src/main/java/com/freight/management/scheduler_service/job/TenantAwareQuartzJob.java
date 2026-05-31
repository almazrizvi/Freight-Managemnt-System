package com.freight.management.scheduler_service.job;

import com.freight.management.core.context.TenantContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class TenantAwareQuartzJob implements Job {

    public static final String TENANT_ID_KEY = "tenantId";
    public static final String JOB_DEFINITION_ID_KEY = "jobDefinitionId";
    public static final String PAYLOAD_JSON_KEY = "payloadJson";

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String tenantId = jobDataMap.getString(TENANT_ID_KEY);
        if (tenantId == null || tenantId.isBlank()) {
            throw new JobExecutionException("Tenant ID is required for tenant-aware jobs");
        }

        TenantContext.setTenantId(tenantId);
        try {
            executeForTenant(context);
        } finally {
            TenantContext.clear();
        }
    }

    protected abstract void executeForTenant(JobExecutionContext context) throws JobExecutionException;
}
