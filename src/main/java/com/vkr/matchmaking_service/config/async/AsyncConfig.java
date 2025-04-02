package com.vkr.matchmaking_service.config.async;

import com.vkr.matchmaking_service.controller.handler.AsyncExceptionHandler;
import com.vkr.matchmaking_service.property.AsyncProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    private final AsyncProperty asyncProperty;

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    @Bean
    public Executor kafkaThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperty.getSettings().get("kafka").getCorePoolSize());
        executor.setMaxPoolSize(asyncProperty.getSettings().get("kafka").getMaxPoolSize());
        executor.setQueueCapacity(asyncProperty.getSettings().get("kafka").getQueueCapacity());
        executor.setThreadNamePrefix("KafkaAsyncThread-");
        executor.initialize();
        return executor;
    }

}
