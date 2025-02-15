package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.entity.lobby.Lobby;
import com.vkr.matchmaking_service.service.lobby.LobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class LobbyController {

    private final LobbyService lobbyService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/create")
    public void createLobby(@Payload Map<String, String> data) {
        String mode = data.get("mode");
        String steamId = data.get("steamId");
        Lobby lobby = lobbyService.createLobby(mode, steamId);
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getId(), lobby);
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
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, lobbyService.getLobbyById(lobbyId.toString()));
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
    public List<Lobby> sendAllLobbies() {
        return lobbyService.getAllLobbies();
    }

    @MessageMapping("/getLobby")
    public void sendLobby(@Payload String lobbyId) {
        Lobby lobby = lobbyService.getLobbyById(lobbyId);
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, lobby);
    }
}
