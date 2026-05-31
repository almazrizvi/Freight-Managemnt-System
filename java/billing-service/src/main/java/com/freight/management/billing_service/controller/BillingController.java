package com.freight.management.billing_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/billing")
public class BillingController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "service", "billing-service",
                "status", "UP",
                "message", "Blank billing service is ready",
                "timestamp", OffsetDateTime.now().toString()
        );
    }
}
