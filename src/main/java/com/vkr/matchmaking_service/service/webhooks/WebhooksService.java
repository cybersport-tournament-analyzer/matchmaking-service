package com.vkr.matchmaking_service.service.webhooks;

import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;

import java.io.IOException;

public interface WebhooksService {

    void handleEvent(Match match, String lobbyId) throws IOException, InterruptedException;

    void handleMatchEnd(Match match, String lobbyId) throws IOException, InterruptedException;

    void handleRoundEnd(Match match, String lobbyId) throws IOException, InterruptedException;


    Match getMatchById(String lobbyId);

}