package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.entity.lobby.Lobby;
import com.vkr.matchmaking_service.service.lobby.LobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class LobbyController {

    private final LobbyService lobbyService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/create")
    @SendTo("/topic/lobby")
    public Lobby createLobby(@Payload Map<String, String> data) {
        String mode = data.get("mode");
        String steamId = data.get("steamId");
        Lobby lobby = lobbyService.createLobby(mode, steamId);
        return lobby;
    }

    @MessageMapping("/join")
    @SendTo("/topic/lobby")
    public Lobby joinLobby(@Payload Map<String, String> data) {
        UUID lobbyId = UUID.fromString(data.get("lobbyId"));
        String steamId = data.get("steamId");
        String team = data.get("team");
        lobbyService.addPlayer(lobbyId, steamId, team);
        return lobbyService.getLobbyById(lobbyId.toString());
    }

    @MessageMapping("/leave")
    @SendTo("/topic/lobby")
    public Lobby leaveLobby(@Payload Map<String, String> data) {
        UUID lobbyId = UUID.fromString(data.get("lobbyId"));
        String steamId = data.get("steamId");
        lobbyService.removePlayer(lobbyId, steamId);
        return lobbyService.getLobbyById(lobbyId.toString());
    }

    @MessageMapping("/getLobbies")
    public void sendAllLobbies() {
        List<Lobby> lobbies = lobbyService.getAllLobbies();
        messagingTemplate.convertAndSend("/topic/lobby", lobbies);
    }

    @MessageMapping("/getLobby")
    public void sendLobby(@Payload String lobbyId) {
        Lobby lobby = lobbyService.getLobbyById(lobbyId);
        messagingTemplate.convertAndSend("/topic/lobby", lobby);
    }
}
