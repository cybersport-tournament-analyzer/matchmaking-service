package com.vkr.matchmaking_service.redis.service.ops;

import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.Optional;

public interface RedisOperations {

    <R extends KeyValueRepository<E, ID>, E, ID> Optional<E> findById(R repository, ID id);

    <R extends KeyValueRepository<E, ID>, E, ID> Iterable<E> findAll(R repository);

    <R extends KeyValueRepository<E, ID>, E, ID> void deleteById(R repository, ID id);

    <R extends KeyValueRepository<E, ID>, E, ID> E updateOrSave(R repository, E entity, ID id);
}
