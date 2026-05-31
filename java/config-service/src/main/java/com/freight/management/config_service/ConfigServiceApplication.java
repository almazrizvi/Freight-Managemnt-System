package com.freight.management.config_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigServiceApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ConfigServiceApplication.class);
		// Set native profile as default to ensure it's loaded before config server initialization
		app.setAdditionalProfiles("native");
		app.run(args);
	}

}
