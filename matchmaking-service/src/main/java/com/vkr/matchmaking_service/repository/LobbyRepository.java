package com.vkr.matchmaking_service.repository;

import com.vkr.matchmaking_service.entity.server.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LobbyRepository extends JpaRepository<Lobby, UUID> {
    List<Lobby> findByCreatedAtBefore(LocalDateTime time);
}

