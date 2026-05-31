package com.freight.management.scheduler_service.service;

import com.freight.management.scheduler_service.dto.JobDefinitionRequest;
import com.freight.management.scheduler_service.model.CronMisfirePolicy;
import com.freight.management.scheduler_service.model.ScheduledJobDefinition;
import com.freight.management.scheduler_service.repository.ScheduledJobDefinitionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulerJobServiceTest {

    @Mock
    private ScheduledJobDefinitionRepository repository;
    @Mock
    private JobClassResolver jobClassResolver;
    @Mock
    private QuartzSchedulerService quartzSchedulerService;

    @InjectMocks
    private SchedulerJobService schedulerJobService;

    @Test
    void shouldCreateTenantScopedJobDefinition() {
        JobDefinitionRequest request = new JobDefinitionRequest();
        request.setJobName("heartbeat");
        request.setJobGroup("ops");
        request.setJobClassName("com.freight.management.scheduler_service.job.NoOpTenantAwareJob");
        request.setCronExpression("0 0/5 * * * ?");
        request.setTimeZone("UTC");
        request.setMisfirePolicy(CronMisfirePolicy.DO_NOTHING);

        ScheduledJobDefinition savedDefinition = new ScheduledJobDefinition();
        savedDefinition.setId(UUID.randomUUID());
        savedDefinition.setTenantId("tenant-a");
        savedDefinition.setJobName("heartbeat");
        savedDefinition.setJobGroup("OPS");
        savedDefinition.setJobClassName(request.getJobClassName());
        savedDefinition.setCronExpression(request.getCronExpression());
        savedDefinition.setTimeZone("UTC");
        savedDefinition.setActive(true);
        savedDefinition.setPaused(false);
        savedDefinition.setMisfirePolicy(CronMisfirePolicy.DO_NOTHING);

        when(repository.existsByTenantIdAndJobGroupAndJobName("tenant-a", "OPS", "heartbeat")).thenReturn(false);
        when(repository.save(any(ScheduledJobDefinition.class))).thenReturn(savedDefinition);

        schedulerJobService.createJob("tenant-a", request);

        ArgumentCaptor<ScheduledJobDefinition> captor = ArgumentCaptor.forClass(ScheduledJobDefinition.class);
        verify(repository).save(captor.capture());
        verify(jobClassResolver).resolve(request.getJobClassName());
        verify(quartzSchedulerService).syncJob(savedDefinition);
        assertEquals("tenant-a", captor.getValue().getTenantId());
        assertEquals("OPS", captor.getValue().getJobGroup());
    }

    @Test
    void shouldRejectInvalidCronExpressions() {
        JobDefinitionRequest request = new JobDefinitionRequest();
        request.setJobName("heartbeat");
        request.setJobClassName("com.freight.management.scheduler_service.job.NoOpTenantAwareJob");
        request.setCronExpression("not-a-cron");
        request.setTimeZone("UTC");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> schedulerJobService.createJob("tenant-a", request)
        );

        assertEquals("Invalid Quartz cron expression", exception.getMessage());
    }

    @Test
    void shouldLoadJobsByTenantOnly() {
        UUID jobId = UUID.randomUUID();
        ScheduledJobDefinition definition = new ScheduledJobDefinition();
        definition.setId(jobId);
        definition.setTenantId("tenant-a");
        definition.setJobName("heartbeat");
        definition.setJobGroup("DEFAULT");
        definition.setJobClassName("com.freight.management.scheduler_service.job.NoOpTenantAwareJob");
        definition.setCronExpression("0 0/5 * * * ?");
        definition.setTimeZone("UTC");
        definition.setActive(true);
        definition.setPaused(false);
        definition.setMisfirePolicy(CronMisfirePolicy.SMART_POLICY);

        when(repository.findByIdAndTenantId(jobId, "tenant-a")).thenReturn(Optional.of(definition));

        assertEquals(jobId, schedulerJobService.getJob("tenant-a", jobId).getId());
    }
}
