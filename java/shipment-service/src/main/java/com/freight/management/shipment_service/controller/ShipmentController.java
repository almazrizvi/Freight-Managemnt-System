package com.freight.management.shipment_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "service", "shipment-service",
                "status", "UP",
                "message", "Blank shipment service is ready",
                "timestamp", OffsetDateTime.now().toString()
        );
    }
}
