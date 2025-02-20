package com.vkr.matchmaking_service.service.lobby;

import com.vkr.matchmaking_service.entity.lobby.Lobby;
import com.vkr.matchmaking_service.entity.pickbans.Action;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface LobbyService {

    List<Lobby> getAllLobbies();

    Lobby createLobby(String mode, String format, String steamId);

    void addPlayer(UUID lobbyId, String steamId, String team);

    void removePlayer(UUID lobbyId, String steamId);

    Lobby getLobbyById(String lobbyId);

    void setReady(UUID lobbyId, String steamId, boolean ready);

    boolean checkAndStartPickBan(String lobbyId);

    void processPickBanAction(UUID lobbyId, String steamId, Action actionType, String map, String side);

    void handleTimeout(UUID lobbyId);

    void initializePickBanSession(Lobby lobby);

    void startMatch(Lobby lobby) throws IOException, InterruptedException;

}
