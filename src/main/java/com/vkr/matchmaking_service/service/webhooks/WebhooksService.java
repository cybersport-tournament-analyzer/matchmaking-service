package com.vkr.matchmaking_service.service.webhooks;

import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;

import java.io.IOException;

public interface WebhooksService {

    void handleEvent(Match match, String lobbyId);

    void handleMatchEnd(Match match, String lobbyId) throws IOException, InterruptedException;

    void handleRoundEnd(Match match, String lobbyId);

    void handleServerReady(Match match, String lobbyId);

    void handlePLayerConnected(Server server, String lobbyId);

    void handleAllPlayersConnected(Match match, String lobbyId);

    void handleBootingServer(Match match, String lobbyId);

    void handleLoadingMap(Match match, String lobbyId);

    void handleMatchStarted(Match match, String lobbyId);

    void handlePLayerDisconnected(Server server, String lobbyId);

    void handleMatchCancelled(Match match, String lobbyId);

    Match getMatchById(String lobbyId);

}