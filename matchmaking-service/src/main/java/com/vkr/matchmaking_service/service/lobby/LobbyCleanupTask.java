package com.vkr.matchmaking_service.service.lobby;

import com.vkr.matchmaking_service.entity.server.Lobby;
import com.vkr.matchmaking_service.repository.LobbyRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class LobbyCleanupTask {

    private LobbyRepository lobbyRepository;
    private LobbyService lobbyService;



    @Scheduled(fixedRate = 300_000) //
    @Transactional
    public void cleanOldLobbies() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        List<Lobby> oldLobbies = lobbyRepository.findByCreatedAtBefore(tenMinutesAgo);
        lobbyService.removeExpiredLobbies(oldLobbies);
    }
}
