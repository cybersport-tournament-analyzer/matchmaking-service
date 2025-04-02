package com.vkr.matchmaking_service.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Map;

@Data
@RefreshScope
@ConfigurationProperties(prefix = "async.settings.redis")
public class RedisAsyncProperty {

    private Map<String, AsyncSettings> settings;

    @Data
    public static class AsyncSettings {
        private String name;
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
    }
}
