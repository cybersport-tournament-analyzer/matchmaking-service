package com.vkr.matchmaking_service.repository;

import com.vkr.matchmaking_service.entity.lobby.Lobby;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LobbyRepository extends CrudRepository<Lobby, UUID> {
    List<Lobby> findByCreatedAtBefore(LocalDateTime time);
}


