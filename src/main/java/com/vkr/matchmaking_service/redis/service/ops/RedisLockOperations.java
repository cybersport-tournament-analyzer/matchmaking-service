package com.vkr.matchmaking_service.redis.service.ops;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLockOperations implements RedisOperations {

    private final ExpirableLockRegistry expirableLockRegistry;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String GLOBAL_SCAN_LOCK_KEY = "global_scan_lock";
    private static final String LOBBY_KEY_PREFIX = "lobby:";

    @Override
    @Retryable(retryFor = {OptimisticLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public <R extends KeyValueRepository<E, ID>, E, ID> Optional<E> findById(R repository, ID id) {

        Callable<Optional<E>> callable = () -> repository.findById(id);

        return lock(callable, id.toString());
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public <R extends KeyValueRepository<E, ID>, E, ID> void deleteById(R repository, ID id) {

        Runnable runnable = () -> repository.deleteById(id);

        lock(runnable, id.toString());
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public <R extends KeyValueRepository<E, ID>, E, ID> Iterable<E> findAll(R repository) {
        Callable<Iterable<E>> callable = () -> {
            Set<String> keys = redisTemplate.keys(LOBBY_KEY_PREFIX + "*");
            if (keys == null || keys.isEmpty()) {
                return Collections.emptyList();
            }

            List<E> result = new ArrayList<>();
            for (String key : keys) {
                String idStr = key.substring(LOBBY_KEY_PREFIX.length());
                ID id = (ID) (idStr);

                repository.findById(id).ifPresent(result::add);
            }
            return result;
        };
        return lock(callable, GLOBAL_SCAN_LOCK_KEY);
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public <R extends KeyValueRepository<E, ID>, E, ID> E updateOrSave(R repository, E entity, ID id) {

        Callable<E> callable = () -> repository.save(entity);

        return lock(callable, id.toString());
    }

    private void lock(Runnable operation, String lockKey) {

        Lock lock = expirableLockRegistry.obtain(lockKey);

        if (lock.tryLock()) {
            try {
                operation.run();
            } finally {
                lock.unlock();
            }
        } else {
            throw new OptimisticLockException("Failed to obtain lock for key: " + lockKey);
        }
    }

    private <T> T lock(Callable<T> operation, String lockKey) {

        Lock lock = expirableLockRegistry.obtain(lockKey);

        if (lock.tryLock()) {
            try {
                try {
                    return operation.call();
                } catch (Exception e) {
                    log.warn("Failed to execute operation: ", e);
                    return null;
                }
            } finally {
                lock.unlock();
            }
        } else {
            throw new OptimisticLockException("Failed to obtain lock for key: " + lockKey);
        }
    }
}
