package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.dto.lobby.AddUserToLobbyDto;
import com.vkr.matchmaking_service.dto.lobby.CreateLobbyDto;
import com.vkr.matchmaking_service.dto.lobby.RemoveUserFromLobbyDto;
import com.vkr.matchmaking_service.entity.server.Lobby;
import com.vkr.matchmaking_service.service.lobby.LobbyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class LobbyController {

    private final List<String> players = new ArrayList<>();

    @MessageMapping("/join")
    @SendTo("/topic/lobby")
    public List<String> joinLobby(String playerName) {
        System.out.println("playerName");
        System.out.println(playerName);
        if (players.size() < 10 && !players.contains(playerName)) {
            players.add(playerName);
        }
        System.out.println(players);
        return players;
    }

    @MessageMapping("/ready")
    @SendTo("/topic/match")
    public String startMatch() {
        if (players.size() == 10) {
            System.out.println("Сохранение матча в БД...");
            players.clear();
            return "Матч создан!";
        }
        return "Ожидание игроков...";
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
