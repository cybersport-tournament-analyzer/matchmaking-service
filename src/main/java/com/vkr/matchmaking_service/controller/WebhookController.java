package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.service.webhooks.WebhooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhooksService webhooksService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/event/{lobbyId}")
    public ResponseEntity<Void> handleEvent(@RequestBody Match match, @PathVariable String lobbyId) {
        webhooksService.handleEvent(match, lobbyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/match-end/{lobbyId}")
    public ResponseEntity<Void> handleMatchEnd(@RequestBody Match match, @PathVariable String lobbyId) {
        webhooksService.handleMatchEnd(match, lobbyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/round-end/{lobbyId}")
    public ResponseEntity<Void> handleRoundEnd(@RequestBody Match match, @PathVariable String lobbyId) {
        webhooksService.handleRoundEnd(match, lobbyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/match-start/{lobbyId}")
    public ResponseEntity<Void> handleMatchStart(@RequestBody Match match, @PathVariable String lobbyId) {
        webhooksService.handleMatchStarted(match, lobbyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/match-cancel/{lobbyId}")
    public ResponseEntity<Void> handleMatchCancel(@RequestBody Match match, @PathVariable String lobbyId) {
        webhooksService.handleMatchCancelled(match, lobbyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/server-booting/{lobbyId}")
    public ResponseEntity<Void> handleServerBooting(@RequestBody Match match, @PathVariable String lobbyId) {
        webhooksService.handleBootingServer(match, lobbyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/players-ready/{lobbyId}")
    public ResponseEntity<Void> handleAllPLayersReady(@RequestBody Match match, @PathVariable String lobbyId) {
        webhooksService.handleAllPlayersConnected(match, lobbyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/map-loading/{lobbyId}")
    public ResponseEntity<Void> handleMapLoading(@RequestBody Match match, @PathVariable String lobbyId) {
        webhooksService.handleServerReady(match, lobbyId);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/getMatch")
    public void sendLobby(@Payload Map<String, String> data) {
        String lobbyId = data.get("lobbyId");
        Match currentMatch = webhooksService.getMatchById(lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, currentMatch);
    }
}
