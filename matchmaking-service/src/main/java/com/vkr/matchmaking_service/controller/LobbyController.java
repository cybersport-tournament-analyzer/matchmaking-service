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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lobby")
@RequiredArgsConstructor
@Slf4j
public class LobbyController {

    private final LobbyService lobbyService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all lobbies")
    public List<Lobby> getAllLobbies() {
        return lobbyService.getAllLobbies();
    }

    @GetMapping("/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get lobby by id")
    public Lobby getLobbyById(@PathVariable String lobbyId) {
        return lobbyService.getLobbyById(lobbyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create lobby")
    public Lobby createLobby(@RequestBody CreateLobbyDto createLobbyDto) {
        return lobbyService.createLobby(createLobbyDto.getMode(), createLobbyDto.getSteamId());
    }

    @PostMapping("/{lobbyId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add player")
    public void addPlayer(@RequestBody AddUserToLobbyDto dto, @PathVariable String lobbyId) {
        lobbyService.addPlayer(UUID.fromString(lobbyId), dto.getSteamId(), dto.getTeam());
    }

    @DeleteMapping("/{lobbyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete player from team")
    public void deletePlayer(@PathVariable String lobbyId, @RequestBody RemoveUserFromLobbyDto dto){
        lobbyService.removePlayer(UUID.fromString(lobbyId), dto.getSteamId());
    }
}
