package com.vkr.matchmaking_service.service.webhooks;

import com.vkr.matchmaking_service.dto.lobby.CreateMatchDto;
import com.vkr.matchmaking_service.entity.lobby.Lobby;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;
import com.vkr.matchmaking_service.exception.MatchNotFoundException;
import com.vkr.matchmaking_service.service.lobby.LobbyService;
import com.vkr.matchmaking_service.service.server.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhooksServiceImpl implements WebhooksService {

    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyService lobbyService;
    private final ServerService serverService;

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

    private void updateEndedMatch(Match match, String lobbyId) throws IOException, InterruptedException {
        Lobby currentLobby = lobbyService.getLobbyById(lobbyId);
        Match currentMatch = currentLobby.getMatches().stream().
                filter(match1 -> match1.getId().equals(match.getId())).findFirst().orElseThrow(
                        () -> new MatchNotFoundException("Match not found in lobby: " + lobbyId)
                );
        currentLobby.getMatches().remove(currentMatch);
        currentLobby.getMatches().add(match);

        if (match.getTeam1().getStats().getScore() > match.getTeam2().getStats().getScore()) {
            if (match.getTeam1().getName().equals(currentLobby.getTeam1Name())) {
                currentLobby.setTeam1Score(currentLobby.getTeam1Score() + 1);
            } else {
                currentLobby.setTeam2Score(currentLobby.getTeam2Score() + 1);
            }
        } else if(match.getTeam1().getStats().getScore() < match.getTeam2().getStats().getScore()){
            if (match.getTeam2().getName().equals(currentLobby.getTeam2Name())) {
                currentLobby.setTeam2Score(currentLobby.getTeam2Score() + 1);
            } else {
                currentLobby.setTeam1Score(currentLobby.getTeam1Score() + 1);
            }
        }
        switch (currentLobby.getFormat()) {
            case "bo1" -> serverService.stopServer(getMatchById(lobbyId).getGame_server_id());
            case "bo3" -> {
                currentLobby.setCurrentMapNumber(currentLobby.getCurrentMapNumber() + 1);
                if (currentLobby.getTeam1Score() == 2 || currentLobby.getTeam2Score() == 2)
                    serverService.stopServer(getMatchById(lobbyId).getGame_server_id());
                else {
                    lobbyService.startMatch(currentLobby);
                }
            }
            case "bo5" -> {
                currentLobby.setCurrentMapNumber(currentLobby.getCurrentMapNumber() + 1);
                if (currentLobby.getTeam1Score() == 3 || currentLobby.getTeam2Score() == 3)
                    serverService.stopServer(getMatchById(lobbyId).getGame_server_id());
                else {
                    lobbyService.startMatch(currentLobby);
                }
            }
        }
        lobbyService.save(currentLobby);
    }

    private CreateMatchDto matchToDto(Match match, String lobbyId) {
        Lobby currentLobby = lobbyService.getLobbyById(lobbyId);
        CreateMatchDto createMatchDto = new CreateMatchDto();
        createMatchDto.setMatch(match);
        createMatchDto.setFormat(currentLobby.getFormat());
        createMatchDto.setMode(currentLobby.getMode());
        createMatchDto.setTeam1Score(currentLobby.getTeam1Score());
        createMatchDto.setTeam2Score(currentLobby.getTeam2Score());
        createMatchDto.setTeam1Name(currentLobby.getTeam1Name());
        createMatchDto.setTeam2Name(currentLobby.getTeam2Name());
        return createMatchDto;
    }

    @Override
    public void handleEvent(Match match, String lobbyId) {
        log.info("handleEvent: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, matchToDto(match, lobbyId));
    }

    @Override
    public void handleMatchEnd(Match match, String lobbyId) throws IOException, InterruptedException {
        log.info("match end: " + match);
        updateEndedMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, matchToDto(match, lobbyId));
    }

    @Override
    public void handleRoundEnd(Match match, String lobbyId) {
        log.info("round end: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, matchToDto(match, lobbyId));
    }

    @Override
    public void handleServerReady(Match match, String lobbyId) {
        log.info("Server ready: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, matchToDto(match, lobbyId));
    }

    @Override
    public void handlePLayerConnected(Server server, String lobbyId) {
        log.info("Player connected: ");
    }

    @Override
    public void handleAllPlayersConnected(Match match, String lobbyId) {
        log.info("All players connected: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, matchToDto(match, lobbyId));
    }

    @Override
    public void handleBootingServer(Match match, String lobbyId) {
        log.info("Server is booting rn: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, matchToDto(match, lobbyId));
    }

    @Override
    public void handleLoadingMap(Match match, String lobbyId) {
        log.info("Map loading: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, matchToDto(match, lobbyId));
    }

    @Override
    public void handleMatchStarted(Match match, String lobbyId) {
        log.info("match started: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, matchToDto(match, lobbyId));
    }

    @Override
    public void handlePLayerDisconnected(Server server, String lobbyId) {
        log.info("Player disconnected: ");
    }

    @Override
    public void handleMatchCancelled(Match match, String lobbyId) {
        log.info("match cancelled: " + match);
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, matchToDto(match, lobbyId));
    }

    @Override
    public Match getMatchById(String lobbyId) {
        Lobby currentLobby = lobbyService.getLobbyById(lobbyId);
        return currentLobby.getMatches().get(currentLobby.getMatches().size() - 1);
    }
}
