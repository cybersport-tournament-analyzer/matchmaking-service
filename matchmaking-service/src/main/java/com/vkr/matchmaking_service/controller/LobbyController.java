package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.dto.lobby.CreateLobbyDto;
import com.vkr.matchmaking_service.dto.match.MatchSettingsDto;
import com.vkr.matchmaking_service.dto.match.MatchStartingDto;
import com.vkr.matchmaking_service.dto.match.StartMatchPlayerDto;
import com.vkr.matchmaking_service.dto.user.UserDto;
import com.vkr.matchmaking_service.entity.lobby.Lobby;
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

    private static final Logger log = LoggerFactory.getLogger(LobbyController.class);
    private final LobbyService lobbyService;
    private final ServerService serverService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/create")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Create lobby")
    public String createLobby(@RequestBody CreateLobbyDto data) {
        String mode = data.getMode();
        String steamId = data.getSteamId();
        Lobby lobby = lobbyService.createLobby(mode, steamId);
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
    public void setPlayerReady(@Payload Map<String, String> data) throws IOException, InterruptedException {
        UUID lobbyId = UUID.fromString(data.get("lobbyId"));
        String steamId = data.get("steamId");
        boolean ready = Boolean.parseBoolean(data.get("ready"));

        lobbyService.setReady(lobbyId, steamId, ready);
        if (lobbyService.checkAndStartPickBan(String.valueOf(lobbyId))){
            String id = serverService.getAvailableServer().getId();
            serverService.startServer(id);
            MatchStartingDto matchStartingDto = new MatchStartingDto();
            matchStartingDto.setGame_server_id(id);
            MatchSettingsDto matchSettingsDto = new MatchSettingsDto();
            matchSettingsDto.setMap("de_inferno");
            matchSettingsDto.setTeam_size(lobbyService.getLobbyById(String.valueOf(lobbyId)).maxPlayersPerTeam());
            matchSettingsDto.setPassword("");
            List<StartMatchPlayerDto> startMatchPlayerDtoList = new ArrayList<>();
            for(UserDto userDto : lobbyService.getLobbyById(String.valueOf(lobbyId)).getTeam1()){
                StartMatchPlayerDto startMatchPlayerDto = new StartMatchPlayerDto();
                startMatchPlayerDto.setTeam("team1");
                startMatchPlayerDto.setSteam_id_64(userDto.getSteamId());
                startMatchPlayerDtoList.add(startMatchPlayerDto);
            }
            for(UserDto userDto : lobbyService.getLobbyById(String.valueOf(lobbyId)).getTeam2()){
                StartMatchPlayerDto startMatchPlayerDto = new StartMatchPlayerDto();
                startMatchPlayerDto.setTeam("team2");
                startMatchPlayerDto.setSteam_id_64(userDto.getSteamId());
                startMatchPlayerDtoList.add(startMatchPlayerDto);
            }
            matchStartingDto.setPlayers(startMatchPlayerDtoList);
            matchStartingDto.setSettings(matchSettingsDto);
            serverService.startMatch(matchStartingDto);
        }
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
}
