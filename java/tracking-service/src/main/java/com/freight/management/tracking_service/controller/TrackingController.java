package com.freight.management.tracking_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/tracking")
public class TrackingController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "service", "tracking-service",
                "status", "UP",
                "message", "Blank tracking service is ready",
                "timestamp", OffsetDateTime.now().toString()
        );
    }
}
