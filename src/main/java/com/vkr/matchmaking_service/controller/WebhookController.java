package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.dto.lobby.CreateMatchDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.redis.cache.lobby.Lobby;
import com.vkr.matchmaking_service.redis.service.lobby.LobbyService;
import com.vkr.matchmaking_service.service.webhooks.WebhooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhooksService webhooksService;
    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyService lobbyService;

    @PostMapping("/event/{lobbyId}")
    public ResponseEntity<Void> handleEvent(@RequestBody Match match, @PathVariable String lobbyId) throws IOException, InterruptedException {
        webhooksService.handleEvent(match, lobbyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/match-end/{lobbyId}")
    public ResponseEntity<Void> handleMatchEnd(@RequestBody Match match, @PathVariable String lobbyId) throws IOException, InterruptedException {
        webhooksService.handleMatchEnd(match, lobbyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/round-end/{lobbyId}")
    public ResponseEntity<Void> handleRoundEnd(@RequestBody Match match, @PathVariable String lobbyId) throws IOException, InterruptedException {
        webhooksService.handleRoundEnd(match, lobbyId);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/getMatch")
    public void sendLobby(@Payload String lobbyId) {
        Lobby currentLobby = lobbyService.getLobbyById(lobbyId);
        CreateMatchDto createMatchDto = new CreateMatchDto();
        createMatchDto.setMatch(webhooksService.getMatchById(lobbyId));
        createMatchDto.setFormat(currentLobby.getFormat());
        createMatchDto.setMode(currentLobby.getMode());
        createMatchDto.setTeam1Score(currentLobby.getTeam1Score());
        createMatchDto.setTeam2Score(currentLobby.getTeam2Score());
        createMatchDto.setTeam1Name(currentLobby.getTeam1Name());
        createMatchDto.setTeam2Name(currentLobby.getTeam2Name());
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, createMatchDto);
    }
}

