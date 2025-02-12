package com.vkr.matchmaking_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling

@EnableCaching
@EnableRedisRepositories(
		enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP,
		shadowCopy = RedisKeyValueAdapter.ShadowCopy.OFF)
public class MatchmakingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchmakingServiceApplication.class, args);
	}

}
