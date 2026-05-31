package com.freight.management.scheduler_service.service;

import com.freight.management.scheduler_service.job.SchedulerJobComponent;
import org.quartz.Job;
import org.springframework.stereotype.Component;

@Component
public class JobClassResolver {

    private static final String ALLOWED_PACKAGE_PREFIX = "com.freight.management.scheduler_service.job";

    public Class<? extends Job> resolve(String className) {
        if (className == null || className.isBlank()) {
            throw new IllegalArgumentException("Job class name is required");
        }

        try {
            Class<?> loadedClass = Class.forName(className.trim(), true, Thread.currentThread().getContextClassLoader());
            if (!Job.class.isAssignableFrom(loadedClass)) {
                throw new IllegalArgumentException("Configured class does not implement Quartz Job: " + className);
            }

            if (!loadedClass.getPackageName().startsWith(ALLOWED_PACKAGE_PREFIX)) {
                throw new IllegalArgumentException("Job class is outside the allowed scheduler job package: " + className);
            }

            if (!loadedClass.isAnnotationPresent(SchedulerJobComponent.class)) {
                throw new IllegalArgumentException("Job class must be annotated with @SchedulerJobComponent: " + className);
            }

            return loadedClass.asSubclass(Job.class);
        } catch (ClassNotFoundException exception) {
            throw new IllegalArgumentException("Job class not found: " + className, exception);
        }
    }
}
