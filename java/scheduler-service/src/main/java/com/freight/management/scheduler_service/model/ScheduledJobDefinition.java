package com.freight.management.scheduler_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "scheduler_job_definition",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_scheduler_job_definition_tenant_group_name",
                columnNames = {"tenant_id", "job_group", "job_name"}
        )
)
@Data
@NoArgsConstructor
public class ScheduledJobDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;

    @Column(name = "job_name", nullable = false, length = 150)
    private String jobName;

    @Column(name = "job_group", nullable = false, length = 150)
    private String jobGroup = "DEFAULT";

    @Column(length = 500)
    private String description;

    @Column(name = "job_class_name", nullable = false, length = 255)
    private String jobClassName;

    @Column(name = "cron_expression", nullable = false, length = 120)
    private String cronExpression;

    @Column(name = "time_zone", nullable = false, length = 100)
    private String timeZone = "UTC";

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "is_paused", nullable = false)
    private Boolean paused = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "misfire_policy", nullable = false, length = 40)
    private CronMisfirePolicy misfirePolicy = CronMisfirePolicy.SMART_POLICY;

    @Column(name = "payload_json", columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (jobGroup == null || jobGroup.isBlank()) {
            jobGroup = "DEFAULT";
        }
        if (timeZone == null || timeZone.isBlank()) {
            timeZone = "UTC";
        }
        if (active == null) {
            active = true;
        }
        if (paused == null) {
            paused = false;
        }
        if (misfirePolicy == null) {
            misfirePolicy = CronMisfirePolicy.SMART_POLICY;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (jobGroup == null || jobGroup.isBlank()) {
            jobGroup = "DEFAULT";
        }
        if (timeZone == null || timeZone.isBlank()) {
            timeZone = "UTC";
        }
        if (misfirePolicy == null) {
            misfirePolicy = CronMisfirePolicy.SMART_POLICY;
        }
    }
}
