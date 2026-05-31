package com.freight.management.scheduler_service.dto;

import com.freight.management.scheduler_service.model.CronMisfirePolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobDefinitionRequest {

    @NotBlank
    @Size(max = 150)
    private String jobName;

    @Size(max = 150)
    private String jobGroup = "DEFAULT";

    @Size(max = 500)
    private String description;

    @NotBlank
    @Size(max = 255)
    private String jobClassName;

    @NotBlank
    @Size(max = 120)
    private String cronExpression;

    @NotBlank
    @Size(max = 100)
    private String timeZone = "UTC";

    private Boolean active = true;

    private Boolean paused = false;

    private CronMisfirePolicy misfirePolicy = CronMisfirePolicy.SMART_POLICY;

    private String payloadJson;
}
