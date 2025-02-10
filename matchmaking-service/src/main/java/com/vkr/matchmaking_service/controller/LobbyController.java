package com.vkr.matchmaking_service.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LobbyController {

    private final List<String> players = new ArrayList<>();
    private final SimpMessagingTemplate messagingTemplate;

    public LobbyController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/join")
    @SendTo("/topic/lobby")
    public List<String> joinLobby(String playerName) {
        if (!players.contains(playerName) && players.size() < 10) {
            players.add(playerName);
        }
        return players;
    }

    @MessageMapping("/leave")
    @SendTo("/topic/lobby")
    public List<String> leaveLobby(String playerName) {
        players.remove(playerName);
        return players;
    }

    // Новый метод: отправка текущего состояния лобби при подключении
    @MessageMapping("/getPlayers")
    public void sendCurrentPlayers() {
        messagingTemplate.convertAndSend("/topic/lobby", players);
    }

//    private final LobbyService lobbyService;
//
//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get all lobbies")
//    public List<Lobby> getAllLobbies() {
//        return lobbyService.getAllLobbies();
//    }
//
//    @GetMapping("/{lobbyId}")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get lobby by id")
//    public Lobby getLobbyById(@PathVariable String lobbyId) {
//        return lobbyService.getLobbyById(lobbyId);
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    @Operation(summary = "Create lobby")
//    public Lobby createLobby(@RequestBody CreateLobbyDto createLobbyDto) {
//        return lobbyService.createLobby(createLobbyDto.getMode(), createLobbyDto.getSteamId());
//    }
//
//    @PostMapping("/{lobbyId}")
//    @ResponseStatus(HttpStatus.CREATED)
//    @Operation(summary = "Add player")
//    public void addPlayer(@RequestBody AddUserToLobbyDto dto, @PathVariable String lobbyId) {
//        lobbyService.addPlayer(UUID.fromString(lobbyId), dto.getSteamId(), dto.getTeam());
//    }
//
//    @DeleteMapping("/{lobbyId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @Operation(summary = "Delete player from team")
//    public void deletePlayer(@PathVariable String lobbyId, @RequestBody RemoveUserFromLobbyDto dto){
//        lobbyService.removePlayer(UUID.fromString(lobbyId), dto.getSteamId());
//    }
}
