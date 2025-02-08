package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.dto.lobby.CreateLobbyDto;
import com.vkr.matchmaking_service.entity.server.Lobby;
import com.vkr.matchmaking_service.service.lobby.LobbyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lobby")
@RequiredArgsConstructor
@Slf4j
public class LobbyController {

    private final LobbyService lobbyService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all lobbies")
    public List<Lobby> getAllLobbies(){
        return lobbyService.getAllLobbies();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Create lobby")
    public Lobby createLobby(@RequestBody CreateLobbyDto createLobbyDto){
        return lobbyService.createLobby(createLobbyDto.getMode(), createLobbyDto.getSteamId());
    }
}
