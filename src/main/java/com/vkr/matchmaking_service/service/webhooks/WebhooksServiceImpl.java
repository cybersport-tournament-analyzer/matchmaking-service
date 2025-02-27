package com.vkr.matchmaking_service.service.webhooks;

import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhooksServiceImpl implements WebhooksService {

    private final SimpMessagingTemplate messagingTemplate;


    @Override
    public void handleEvent(Match match) {
        log.info("handleEvent: " + match);
        messagingTemplate.convertAndSend("/topic/match", match);
    }

    @Override
    public void handleMatchEnd(Match match) {
        log.info("match end: " + match);
        messagingTemplate.convertAndSend("/topic/match", match);
    }

    @Override
    public void handleRoundEnd(Match match) {
        log.info("round end: " + match);
        messagingTemplate.convertAndSend("/topic/match", match);
    }

    @Override
    public void handleServerReady(Match match) {
        log.info("Server ready: " + match);
        messagingTemplate.convertAndSend("/topic/match", match);
    }

    @Override
    public void handlePLayerConnected(Server server) {
        log.info("Player connected: ");
    }

    @Override
    public void handleAllPlayersConnected(Match match) {
        log.info("All players connected: " + match);
        messagingTemplate.convertAndSend("/topic/match", match);
    }

    @Override
    public void handleBootingServer(Match match) {
        log.info("Server is booting rn: " + match);
        messagingTemplate.convertAndSend("/topic/match", match);
    }

    @Override
    public void handleLoadingMap(Match match) {
        log.info("Map loading: " + match);
        messagingTemplate.convertAndSend("/topic/match", match);
    }

    @Override
    public void handleMatchStarted(Match match) {
        log.info("match started: " + match);
        messagingTemplate.convertAndSend("/topic/match", match);
    }

    @Override
    public void handlePLayerDisconnected(Server server) {
        log.info("Player disconnected: ");
    }

    @Override
    public void handleMatchCancelled(Match match) {
        log.info("match cancelled: " + match);
        messagingTemplate.convertAndSend("/topic/match", match);
    }
}
