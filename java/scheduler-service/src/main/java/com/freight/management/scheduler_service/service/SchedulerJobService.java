package com.freight.management.scheduler_service.service;

import com.freight.management.scheduler_service.dto.JobDefinitionRequest;
import com.freight.management.scheduler_service.dto.JobDefinitionResponse;
import com.freight.management.scheduler_service.dto.SchedulerSyncResponse;
import com.freight.management.scheduler_service.model.ScheduledJobDefinition;
import com.freight.management.scheduler_service.repository.ScheduledJobDefinitionRepository;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class SchedulerJobService {

    private final ScheduledJobDefinitionRepository jobDefinitionRepository;
    private final JobClassResolver jobClassResolver;
    private final QuartzSchedulerService quartzSchedulerService;

    public SchedulerJobService(
            ScheduledJobDefinitionRepository jobDefinitionRepository,
            JobClassResolver jobClassResolver,
            QuartzSchedulerService quartzSchedulerService
    ) {
        this.jobDefinitionRepository = jobDefinitionRepository;
        this.jobClassResolver = jobClassResolver;
        this.quartzSchedulerService = quartzSchedulerService;
    }

    @Transactional(readOnly = true)
    public List<JobDefinitionResponse> listJobs(String tenantId) {
        String normalizedTenantId = normalizeTenantId(tenantId);
        return jobDefinitionRepository.findByTenantIdOrderByJobGroupAscJobNameAsc(normalizedTenantId).stream()
                .map(JobDefinitionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public JobDefinitionResponse getJob(String tenantId, UUID jobId) {
        return JobDefinitionResponse.from(requireJob(normalizeTenantId(tenantId), jobId));
    }

    public JobDefinitionResponse createJob(String tenantId, JobDefinitionRequest request) {
        String normalizedTenantId = normalizeTenantId(tenantId);
        String normalizedGroup = normalizeGroup(request.getJobGroup());
        if (jobDefinitionRepository.existsByTenantIdAndJobGroupAndJobName(normalizedTenantId, normalizedGroup, normalizeName(request.getJobName()))) {
            throw new IllegalArgumentException("A job with the same tenant, group, and name already exists");
        }

        ScheduledJobDefinition definition = new ScheduledJobDefinition();
        definition.setTenantId(normalizedTenantId);
        applyRequest(definition, request);

        ScheduledJobDefinition savedDefinition = jobDefinitionRepository.save(definition);
        quartzSchedulerService.syncJob(savedDefinition);
        return JobDefinitionResponse.from(savedDefinition);
    }

    public JobDefinitionResponse updateJob(String tenantId, UUID jobId, JobDefinitionRequest request) {
        String normalizedTenantId = normalizeTenantId(tenantId);
        ScheduledJobDefinition definition = requireJob(normalizedTenantId, jobId);
        String normalizedGroup = normalizeGroup(request.getJobGroup());
        String normalizedName = normalizeName(request.getJobName());
        if (jobDefinitionRepository.existsByTenantIdAndJobGroupAndJobNameAndIdNot(
                normalizedTenantId,
                normalizedGroup,
                normalizedName,
                jobId
        )) {
            throw new IllegalArgumentException("A job with the same tenant, group, and name already exists");
        }

        applyRequest(definition, request);
        ScheduledJobDefinition savedDefinition = jobDefinitionRepository.save(definition);
        quartzSchedulerService.syncJob(savedDefinition);
        return JobDefinitionResponse.from(savedDefinition);
    }

    public void deleteJob(String tenantId, UUID jobId) {
        ScheduledJobDefinition definition = requireJob(normalizeTenantId(tenantId), jobId);
        quartzSchedulerService.deleteJob(definition);
        jobDefinitionRepository.delete(definition);
    }

    public JobDefinitionResponse pauseJob(String tenantId, UUID jobId) {
        ScheduledJobDefinition definition = requireJob(normalizeTenantId(tenantId), jobId);
        definition.setPaused(true);
        definition.setActive(true);
        ScheduledJobDefinition savedDefinition = jobDefinitionRepository.save(definition);
        quartzSchedulerService.syncJob(savedDefinition);
        return JobDefinitionResponse.from(savedDefinition);
    }

    public JobDefinitionResponse resumeJob(String tenantId, UUID jobId) {
        ScheduledJobDefinition definition = requireJob(normalizeTenantId(tenantId), jobId);
        definition.setActive(true);
        definition.setPaused(false);
        ScheduledJobDefinition savedDefinition = jobDefinitionRepository.save(definition);
        quartzSchedulerService.syncJob(savedDefinition);
        return JobDefinitionResponse.from(savedDefinition);
    }

    public void triggerJob(String tenantId, UUID jobId) {
        ScheduledJobDefinition definition = requireJob(normalizeTenantId(tenantId), jobId);
        if (!Boolean.TRUE.equals(definition.getActive())) {
            throw new IllegalArgumentException("Only active jobs can be triggered");
        }
        quartzSchedulerService.triggerNow(definition);
    }

    public SchedulerSyncResponse syncTenantJobs(String tenantId) {
        return quartzSchedulerService.syncTenantJobs(normalizeTenantId(tenantId));
    }

    private ScheduledJobDefinition requireJob(String tenantId, UUID jobId) {
        return jobDefinitionRepository.findByIdAndTenantId(jobId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Scheduler job not found for tenant"));
    }

    private void applyRequest(ScheduledJobDefinition definition, JobDefinitionRequest request) {
        validateRequest(request);
        definition.setJobName(normalizeName(request.getJobName()));
        definition.setJobGroup(normalizeGroup(request.getJobGroup()));
        definition.setDescription(normalizeDescription(request.getDescription()));
        definition.setJobClassName(request.getJobClassName().trim());
        definition.setCronExpression(request.getCronExpression().trim());
        definition.setTimeZone(request.getTimeZone().trim());
        definition.setActive(request.getActive() == null || request.getActive());
        definition.setPaused(request.getPaused() != null && request.getPaused());
        definition.setMisfirePolicy(request.getMisfirePolicy());
        definition.setPayloadJson(normalizePayload(request.getPayloadJson()));
    }

    private void validateRequest(JobDefinitionRequest request) {
        if (!CronExpression.isValidExpression(request.getCronExpression())) {
            throw new IllegalArgumentException("Invalid Quartz cron expression");
        }

        try {
            ZoneId.of(request.getTimeZone().trim());
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid time zone: " + request.getTimeZone(), exception);
        }

        jobClassResolver.resolve(request.getJobClassName());
    }

    private String normalizeTenantId(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("X-Tenant-Id header is required");
        }
        return tenantId.trim();
    }

    private String normalizeName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Job name is required");
        }
        return value.trim();
    }

    private String normalizeGroup(String value) {
        return (value == null || value.isBlank()) ? "DEFAULT" : value.trim().toUpperCase();
    }

    private String normalizeDescription(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String normalizePayload(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
