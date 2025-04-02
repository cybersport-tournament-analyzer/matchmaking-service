package com.vkr.matchmaking_service.controller.handler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(@NonNull Throwable ex, Method method, @SuppressWarnings("NullableProblems") Object... params) {
        log.error("Unexpected asynchronous exception at : {}.{}", method.getDeclaringClass().getName(), method.getName(), ex);
    }

}
