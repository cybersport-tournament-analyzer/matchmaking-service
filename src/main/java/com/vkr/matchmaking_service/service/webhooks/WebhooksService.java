package com.vkr.matchmaking_service.service.webhooks;

import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;

public interface WebhooksService {

    void handleEvent(Match match);

    void handleMatchEnd(Match match);

    void handleRoundEnd(Match match);

    void handleServerReady(Server server);

    void handlePLayerConnected(Server server);

    void handleAllPlayersConnected(Server server);

    void handleBootingServer(Server server);

    void handleLoadingMap(Server server);

    void handleMatchStarted(Match match);

    void handlePLayerDisconnected(Server server);

    void handleMatchCancelled(Match match);



}