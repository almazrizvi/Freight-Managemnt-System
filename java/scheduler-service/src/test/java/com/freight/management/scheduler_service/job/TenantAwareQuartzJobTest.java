package com.freight.management.scheduler_service.job;

import com.freight.management.core.context.TenantContext;
import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TenantAwareQuartzJobTest {

    @Test
    void shouldSetAndClearTenantContextAroundExecution() throws Exception {
        JobExecutionContext context = mock(JobExecutionContext.class);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(TenantAwareQuartzJob.TENANT_ID_KEY, "tenant-a");
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        RecordingJob job = new RecordingJob();

        job.execute(context);

        assertEquals("tenant-a", job.capturedTenantId);
        assertTrue(TenantContext.getTenantId().isEmpty());
    }

    private static class RecordingJob extends TenantAwareQuartzJob {
        private String capturedTenantId;

        @Override
        protected void executeForTenant(JobExecutionContext context) throws JobExecutionException {
            capturedTenantId = TenantContext.getTenantId().orElseThrow();
        }
    }
}
