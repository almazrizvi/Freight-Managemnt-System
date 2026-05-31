package com.freight.management.scheduler_service.controller;

import com.freight.management.core.constants.SecurityHeaders;
import com.freight.management.scheduler_service.dto.JobDefinitionRequest;
import com.freight.management.scheduler_service.dto.JobDefinitionResponse;
import com.freight.management.scheduler_service.dto.SchedulerSyncResponse;
import com.freight.management.scheduler_service.service.SchedulerJobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/scheduler/jobs")
public class SchedulerJobController {

    private final SchedulerJobService schedulerJobService;

    public SchedulerJobController(SchedulerJobService schedulerJobService) {
        this.schedulerJobService = schedulerJobService;
    }

    @GetMapping
    public List<JobDefinitionResponse> listJobs(
            @RequestHeader(SecurityHeaders.TENANT_ID) String tenantId
    ) {
        return schedulerJobService.listJobs(tenantId);
    }

    @GetMapping("/{jobId}")
    public JobDefinitionResponse getJob(
            @RequestHeader(SecurityHeaders.TENANT_ID) String tenantId,
            @PathVariable UUID jobId
    ) {
        return schedulerJobService.getJob(tenantId, jobId);
    }

    @PostMapping
    public ResponseEntity<JobDefinitionResponse> createJob(
            @RequestHeader(SecurityHeaders.TENANT_ID) String tenantId,
            @Valid @RequestBody JobDefinitionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(schedulerJobService.createJob(tenantId, request));
    }

    @PutMapping("/{jobId}")
    public JobDefinitionResponse updateJob(
            @RequestHeader(SecurityHeaders.TENANT_ID) String tenantId,
            @PathVariable UUID jobId,
            @Valid @RequestBody JobDefinitionRequest request
    ) {
        return schedulerJobService.updateJob(tenantId, jobId, request);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(
            @RequestHeader(SecurityHeaders.TENANT_ID) String tenantId,
            @PathVariable UUID jobId
    ) {
        schedulerJobService.deleteJob(tenantId, jobId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobId}/pause")
    public JobDefinitionResponse pauseJob(
            @RequestHeader(SecurityHeaders.TENANT_ID) String tenantId,
            @PathVariable UUID jobId
    ) {
        return schedulerJobService.pauseJob(tenantId, jobId);
    }

    @PostMapping("/{jobId}/resume")
    public JobDefinitionResponse resumeJob(
            @RequestHeader(SecurityHeaders.TENANT_ID) String tenantId,
            @PathVariable UUID jobId
    ) {
        return schedulerJobService.resumeJob(tenantId, jobId);
    }

    @PostMapping("/{jobId}/trigger")
    public ResponseEntity<Void> triggerJob(
            @RequestHeader(SecurityHeaders.TENANT_ID) String tenantId,
            @PathVariable UUID jobId
    ) {
        schedulerJobService.triggerJob(tenantId, jobId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/sync")
    public SchedulerSyncResponse syncTenantJobs(
            @RequestHeader(SecurityHeaders.TENANT_ID) String tenantId
    ) {
        return schedulerJobService.syncTenantJobs(tenantId);
    }
}
