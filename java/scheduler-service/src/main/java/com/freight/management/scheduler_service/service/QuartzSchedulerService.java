package com.freight.management.scheduler_service.service;

import com.freight.management.scheduler_service.dto.SchedulerSyncResponse;
import com.freight.management.scheduler_service.job.TenantAwareQuartzJob;
import com.freight.management.scheduler_service.model.CronMisfirePolicy;
import com.freight.management.scheduler_service.model.ScheduledJobDefinition;
import com.freight.management.scheduler_service.repository.ScheduledJobDefinitionRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuartzSchedulerService {

    private static final String GROUP_SEPARATOR = "::";
    private static final String TRIGGER_SUFFIX = "-trigger";

    private final Scheduler scheduler;
    private final ScheduledJobDefinitionRepository jobDefinitionRepository;
    private final JobClassResolver jobClassResolver;

    public QuartzSchedulerService(
            Scheduler scheduler,
            ScheduledJobDefinitionRepository jobDefinitionRepository,
            JobClassResolver jobClassResolver
    ) {
        this.scheduler = scheduler;
        this.jobDefinitionRepository = jobDefinitionRepository;
        this.jobClassResolver = jobClassResolver;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncAllJobsOnStartup() {
        syncAllJobs();
    }

    @Transactional
    public SchedulerSyncResponse syncTenantJobs(String tenantId) {
        String normalizedTenantId = normalizeTenantId(tenantId);
        List<ScheduledJobDefinition> tenantDefinitions = jobDefinitionRepository.findByTenantId(normalizedTenantId);
        int removedJobs = removeOrphanedJobs(
                tenantDefinitions.stream()
                        .filter(definition -> Boolean.TRUE.equals(definition.getActive()))
                        .map(this::buildJobKey)
                        .collect(Collectors.toSet()),
                normalizedTenantId
        );
        int scheduledJobs = 0;
        for (ScheduledJobDefinition definition : tenantDefinitions) {
            if (Boolean.TRUE.equals(definition.getActive())) {
                syncJob(definition);
                scheduledJobs++;
            } else {
                deleteJob(definition);
            }
        }

        return SchedulerSyncResponse.builder()
                .tenantId(normalizedTenantId)
                .scheduledJobs(scheduledJobs)
                .removedJobs(removedJobs)
                .build();
    }

    @Transactional
    public SchedulerSyncResponse syncAllJobs() {
        List<ScheduledJobDefinition> definitions = jobDefinitionRepository.findAll();
        int removedJobs = removeOrphanedJobs(
                definitions.stream()
                        .filter(definition -> Boolean.TRUE.equals(definition.getActive()))
                        .map(this::buildJobKey)
                        .collect(Collectors.toSet()),
                null
        );

        int scheduledJobs = 0;
        for (ScheduledJobDefinition definition : definitions) {
            if (Boolean.TRUE.equals(definition.getActive())) {
                syncJob(definition);
                scheduledJobs++;
            } else {
                deleteJob(definition);
            }
        }

        log.info("Scheduler sync completed: scheduledJobs={}, removedJobs={}", scheduledJobs, removedJobs);
        return SchedulerSyncResponse.builder()
                .tenantId("ALL")
                .scheduledJobs(scheduledJobs)
                .removedJobs(removedJobs)
                .build();
    }

    @Transactional
    public void syncJob(ScheduledJobDefinition definition) {
        if (!Boolean.TRUE.equals(definition.getActive())) {
            deleteJob(definition);
            return;
        }

        try {
            JobKey jobKey = buildJobKey(definition);
            TriggerKey triggerKey = buildTriggerKey(definition);
            JobDetail jobDetail = buildJobDetail(definition);
            CronTrigger trigger = buildCronTrigger(definition, jobKey, triggerKey);

            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }

            scheduler.scheduleJob(jobDetail, trigger);
            if (Boolean.TRUE.equals(definition.getPaused())) {
                scheduler.pauseJob(jobKey);
            }

            definition.setLastSyncedAt(LocalDateTime.now());
            jobDefinitionRepository.save(definition);
        } catch (SchedulerException exception) {
            throw new IllegalStateException("Unable to synchronize Quartz job: " + definition.getJobName(), exception);
        }
    }

    public void triggerNow(ScheduledJobDefinition definition) {
        try {
            scheduler.triggerJob(buildJobKey(definition));
        } catch (SchedulerException exception) {
            throw new IllegalStateException("Unable to trigger Quartz job: " + definition.getJobName(), exception);
        }
    }

    public void deleteJob(ScheduledJobDefinition definition) {
        try {
            JobKey jobKey = buildJobKey(definition);
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException exception) {
            throw new IllegalStateException("Unable to delete Quartz job: " + definition.getJobName(), exception);
        }
    }

    private int removeOrphanedJobs(Set<JobKey> expectedJobKeys, String tenantId) {
        try {
            Set<JobKey> existingKeys = new HashSet<>();
            for (String groupName : scheduler.getJobGroupNames()) {
                existingKeys.addAll(scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName)));
            }

            int removedJobs = 0;
            for (JobKey existingKey : existingKeys) {
                if (!belongsToTenant(existingKey, tenantId)) {
                    continue;
                }
                if (!expectedJobKeys.contains(existingKey)) {
                    scheduler.deleteJob(existingKey);
                    removedJobs++;
                }
            }
            return removedJobs;
        } catch (SchedulerException exception) {
            throw new IllegalStateException("Unable to reconcile Quartz job store", exception);
        }
    }

    private boolean belongsToTenant(JobKey jobKey, String tenantId) {
        if (tenantId == null) {
            return true;
        }
        return jobKey.getGroup().startsWith(normalizeTenantId(tenantId) + GROUP_SEPARATOR);
    }

    private JobDetail buildJobDetail(ScheduledJobDefinition definition) {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(TenantAwareQuartzJob.TENANT_ID_KEY, definition.getTenantId());
        dataMap.put(TenantAwareQuartzJob.JOB_DEFINITION_ID_KEY, definition.getId().toString());
        dataMap.put(TenantAwareQuartzJob.PAYLOAD_JSON_KEY, definition.getPayloadJson() == null ? "" : definition.getPayloadJson());

        return JobBuilder.newJob(jobClassResolver.resolve(definition.getJobClassName()))
                .withIdentity(buildJobKey(definition))
                .withDescription(definition.getDescription())
                .usingJobData(dataMap)
                .build();
    }

    private CronTrigger buildCronTrigger(ScheduledJobDefinition definition, JobKey jobKey, TriggerKey triggerKey) {
        return TriggerBuilder.newTrigger()
                .forJob(jobKey)
                .withIdentity(triggerKey)
                .withSchedule(buildCronSchedule(definition))
                .withDescription(definition.getDescription())
                .build();
    }

    private CronScheduleBuilder buildCronSchedule(ScheduledJobDefinition definition) {
        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(definition.getCronExpression())
                .inTimeZone(TimeZone.getTimeZone(ZoneId.of(definition.getTimeZone())));

        CronMisfirePolicy misfirePolicy = definition.getMisfirePolicy();
        if (misfirePolicy == null) {
            return builder;
        }

        return switch (misfirePolicy) {
            case FIRE_ONCE_NOW -> builder.withMisfireHandlingInstructionFireAndProceed();
            case DO_NOTHING -> builder.withMisfireHandlingInstructionDoNothing();
            case IGNORE_MISFIRES -> builder.withMisfireHandlingInstructionIgnoreMisfires();
            case SMART_POLICY -> builder;
        };
    }

    private JobKey buildJobKey(ScheduledJobDefinition definition) {
        return JobKey.jobKey(definition.getJobName(), buildQuartzGroup(definition.getTenantId(), definition.getJobGroup()));
    }

    private TriggerKey buildTriggerKey(ScheduledJobDefinition definition) {
        return TriggerKey.triggerKey(
                definition.getJobName() + TRIGGER_SUFFIX,
                buildQuartzGroup(definition.getTenantId(), definition.getJobGroup())
        );
    }

    private String buildQuartzGroup(String tenantId, String jobGroup) {
        return normalizeTenantId(tenantId) + GROUP_SEPARATOR + normalizeGroup(jobGroup);
    }

    private String normalizeTenantId(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("X-Tenant-Id header is required");
        }
        return tenantId.trim();
    }

    private String normalizeGroup(String jobGroup) {
        return (jobGroup == null || jobGroup.isBlank()) ? "DEFAULT" : jobGroup.trim().toUpperCase();
    }
}
