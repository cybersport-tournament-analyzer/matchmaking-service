package com.vkr.matchmaking_service.service.lobby;

import com.vkr.matchmaking_service.entity.lobby.Lobby;

import java.util.List;
import java.util.UUID;

public interface LobbyService {

    List<Lobby> getAllLobbies();

    Lobby createLobby(String mode, String steamId);

    void addPlayer(UUID lobbyId, String steamId, String team);

    void removePlayer(UUID lobbyId, String steamId);

    Lobby getLobbyById(String lobbyId);
}
