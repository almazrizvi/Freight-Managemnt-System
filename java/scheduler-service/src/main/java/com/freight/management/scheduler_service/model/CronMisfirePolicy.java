package com.freight.management.scheduler_service.model;

public enum CronMisfirePolicy {
    SMART_POLICY,
    FIRE_ONCE_NOW,
    DO_NOTHING,
    IGNORE_MISFIRES
}
