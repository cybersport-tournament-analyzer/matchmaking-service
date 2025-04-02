package com.vkr.matchmaking_service.redis.repository;

import com.vkr.matchmaking_service.redis.cache.lobby.Lobby;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LobbyRepository extends KeyValueRepository<Lobby, UUID> {
}
