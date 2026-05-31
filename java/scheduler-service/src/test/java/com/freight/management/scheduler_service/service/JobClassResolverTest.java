package com.freight.management.scheduler_service.service;

import com.freight.management.scheduler_service.job.NoOpTenantAwareJob;
import org.junit.jupiter.api.Test;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobClassResolverTest {

    private final JobClassResolver resolver = new JobClassResolver();

    @Test
    void shouldResolveAnnotatedSchedulerJob() {
        Class<?> resolvedClass = resolver.resolve(NoOpTenantAwareJob.class.getName());
        assertEquals(NoOpTenantAwareJob.class, resolvedClass);
    }

    @Test
    void shouldRejectClassesOutsideAllowedPackage() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(ExternalJob.class.getName())
        );

        assertTrue(exception.getMessage().contains("outside the allowed scheduler job package"));
    }

    private static class ExternalJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
        }
    }
}
