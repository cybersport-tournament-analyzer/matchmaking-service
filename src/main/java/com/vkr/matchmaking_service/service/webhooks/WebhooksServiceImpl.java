package com.vkr.matchmaking_service.service.webhooks;

import com.vkr.matchmaking_service.entity.lobby.Lobby;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;
import com.vkr.matchmaking_service.exception.MatchNotFoundException;
import com.vkr.matchmaking_service.service.lobby.LobbyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhooksServiceImpl implements WebhooksService {

    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyService lobbyService;

    private void updateMatch(Match match, String lobbyId) {
        Lobby currentLobby = lobbyService.getLobbyById(lobbyId);
        Match currentMatch = currentLobby.getMatches().stream().
                filter(match1 -> match1.getId().equals(match.getId())).findFirst().orElseThrow(
                        () -> new MatchNotFoundException("Match not found in lobby: " + lobbyId)
                );
        currentLobby.getMatches().remove(currentMatch);
        currentLobby.getMatches().add(match);
        lobbyService.save(currentLobby);
    }

    @Override
    public void handleEvent(Match match, String lobbyId) {
        log.info("handleEvent: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, match);
    }

    @Override
    public void handleMatchEnd(Match match, String lobbyId) {
        log.info("match end: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, match);
    }

    @Override
    public void handleRoundEnd(Match match, String lobbyId) {
        log.info("round end: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, match);
    }

    @Override
    public void handleServerReady(Match match, String lobbyId) {
        log.info("Server ready: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, match);
    }

    @Override
    public void handlePLayerConnected(Server server, String lobbyId) {
        log.info("Player connected: ");
    }

    @Override
    public void handleAllPlayersConnected(Match match, String lobbyId) {
        log.info("All players connected: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, match);
    }

    @Override
    public void handleBootingServer(Match match, String lobbyId) {
        log.info("Server is booting rn: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, match);
    }

    @Override
    public void handleLoadingMap(Match match, String lobbyId) {
        log.info("Map loading: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, match);
    }

    @Override
    public void handleMatchStarted(Match match, String lobbyId) {
        log.info("match started: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, match);
    }

    @Override
    public void handlePLayerDisconnected(Server server, String lobbyId) {
        log.info("Player disconnected: ");
    }

    @Override
    public void handleMatchCancelled(Match match, String lobbyId) {
        log.info("match cancelled: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, match);
    }

    @Override
    public Match getMatchById(String lobbyId) {
        Lobby currentLobby = lobbyService.getLobbyById(lobbyId);
        return currentLobby.getMatches().get(currentLobby.getMatches().size() - 1);
    }
}
