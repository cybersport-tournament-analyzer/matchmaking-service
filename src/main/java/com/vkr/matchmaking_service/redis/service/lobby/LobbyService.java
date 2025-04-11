package com.vkr.matchmaking_service.redis.service.lobby;

import com.vkr.matchmaking_service.dto.tournament_client.player.PlayerDto;
import com.vkr.matchmaking_service.dto.tournament_client.team.TeamDto;
import com.vkr.matchmaking_service.redis.cache.lobby.Lobby;
import com.vkr.matchmaking_service.entity.pickbans.Action;
import com.vkr.matchmaking_service.entity.pickbans.PickBanSession;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.vkr.matchmaking_service.redis.cache.lobby.Lobby;
import com.vkr.matchmaking_service.entity.pickbans.Action;
import com.vkr.matchmaking_service.entity.pickbans.PickBanSession;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface LobbyService {

    List<Lobby> getAllLobbies();

    void createLobby(String mode, String format, TeamDto team1, TeamDto team2, UUID tournamentMatchId, String adminId);

    void addPlayer(UUID lobbyId, PlayerDto player, int slot);

    void removePlayer(UUID lobbyId, String steamId);

    Lobby getLobbyById(String lobbyId);

    void setReady(UUID lobbyId, String steamId, boolean ready);

    boolean checkAndStartPickBan(String lobbyId);

    void processPickBanAction(UUID lobbyId, String steamId, Action actionType, String map, String side) throws IOException, InterruptedException;

    void handleTimeout(UUID lobbyId) throws IOException, InterruptedException;

    void initializePickBanSession(Lobby lobby);

    void startMatch(Lobby lobby) throws IOException, InterruptedException;

    void startTimer(PickBanSession session);

    void stopTimer(PickBanSession session);

    void save(Lobby lobby);

    void deleteLobby(UUID lobbyId);
}
