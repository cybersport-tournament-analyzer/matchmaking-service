package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.dto.lobby.CreateLobbyDto;
import com.vkr.matchmaking_service.dto.match.MatchSettingsDto;
import com.vkr.matchmaking_service.dto.match.MatchStartingDto;
import com.vkr.matchmaking_service.dto.match.StartMatchPlayerDto;
import com.vkr.matchmaking_service.dto.user.UserDto;
import com.vkr.matchmaking_service.entity.lobby.Lobby;
import com.vkr.matchmaking_service.entity.pickbans.Action;
import com.vkr.matchmaking_service.entity.pickbans.PickBanSession;
import com.vkr.matchmaking_service.service.lobby.LobbyService;
import com.vkr.matchmaking_service.service.server.ServerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class LobbyController {

    private final LobbyService lobbyService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/create")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Create lobby")
    public String createLobby(@RequestBody CreateLobbyDto data) {
        String mode = data.getMode();
        String steamId = data.getSteamId();
        String format = data.getFormat();
        Lobby lobby = lobbyService.createLobby(mode, format, steamId);
       return lobby.getId().toString();
    }

    @MessageMapping("/join")
    public void joinLobby(@Payload Map<String, String> data) {
        UUID lobbyId = UUID.fromString(data.get("lobbyId"));
        String steamId = data.get("steamId");
        String team = data.get("team");
        lobbyService.addPlayer(lobbyId, steamId, team);
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, lobbyService.getLobbyById(lobbyId.toString()));
    }

    @MessageMapping("/ready")
    public void setPlayerReady(@Payload Map<String, String> data) {
        UUID lobbyId = UUID.fromString(data.get("lobbyId"));
        String steamId = data.get("steamId");
        boolean ready = Boolean.parseBoolean(data.get("ready"));

        lobbyService.setReady(lobbyId, steamId, ready);

        if (lobbyService.checkAndStartPickBan(String.valueOf(lobbyId))) {
            lobbyService.initializePickBanSession(lobbyService.getLobbyById(lobbyId.toString()));
            messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, lobbyService.getLobbyById(lobbyId.toString()));
        }
    }

    @MessageMapping("/leave")
    public void leaveLobby(@Payload Map<String, String> data) {
        UUID lobbyId = UUID.fromString(data.get("lobbyId"));
        String steamId = data.get("steamId");
        lobbyService.removePlayer(lobbyId, steamId);
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, lobbyService.getLobbyById(lobbyId.toString()));
    }

    @GetMapping("/lobby")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all lobbies")
    public List<Lobby> sendAllLobbies() {
        return lobbyService.getAllLobbies();
    }

    @MessageMapping("/getLobby")
    public void sendLobby(@Payload String lobbyId) {
        Lobby lobby = lobbyService.getLobbyById(lobbyId);
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, lobby);
    }

    @MessageMapping("/pickban")
    public void handlePickBanAction(@Payload Map<String, String> data) throws IOException, InterruptedException {
        UUID lobbyId = UUID.fromString(data.get("lobbyId"));
        String steamId = data.get("steamId");
        Action actionType = Action.valueOf(data.get("actionType"));
        String map = data.get("map");
        String side = data.get("side");

        lobbyService.processPickBanAction(lobbyId, steamId, actionType, map, side);

        Lobby lobby = lobbyService.getLobbyById(lobbyId.toString());
        PickBanSession session = lobby.getPickBanSession();

        if (session.isCompleted()) {
            lobbyService.startMatch(lobby);
        }

        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, session);
    }


}
