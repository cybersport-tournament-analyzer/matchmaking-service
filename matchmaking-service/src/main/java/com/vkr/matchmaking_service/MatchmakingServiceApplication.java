package com.vkr.matchmaking_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MatchmakingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchmakingServiceApplication.class, args);
	}

}
