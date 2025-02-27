package com.vkr.matchmaking_service.service.webhooks;

import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;

public interface WebhooksService {

    void handleEvent(Match match);

    void handleMatchEnd(Match match);

    void handleRoundEnd(Match match);

    void handleServerReady(Match match);

    void handlePLayerConnected(Server server);

    void handleAllPlayersConnected(Match match);

    void handleBootingServer(Match match);

    void handleLoadingMap(Match match);

    void handleMatchStarted(Match match);

    void handlePLayerDisconnected(Server server);

    void handleMatchCancelled(Match match);



}