package com.vkr.matchmaking_service.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Data
@RefreshScope
@ConfigurationProperties(prefix = "spring.data.redis.lock-registry")
public class RedisLockRegistryProperty {

    private String lobbyLockKey;
    private Long releaseTimeDurationMillis;
}
