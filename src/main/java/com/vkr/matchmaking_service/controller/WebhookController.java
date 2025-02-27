package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;
import com.vkr.matchmaking_service.service.webhooks.WebhooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhooksService webhooksService;

    @PostMapping("/event")
    public ResponseEntity<Void> handleEvent(@RequestBody Match match) {
        webhooksService.handleEvent(match);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/match-end")
    public ResponseEntity<Void> handleMatchEnd(@RequestBody Match match) {
        webhooksService.handleMatchEnd(match);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/round-end")
    public ResponseEntity<Void> handleRoundEnd(@RequestBody Match match) {
        webhooksService.handleRoundEnd(match);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/match-start")
    public ResponseEntity<Void> handleMatchStart(@RequestBody Match match) {
        webhooksService.handleMatchStarted(match);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/match-cancel")
    public ResponseEntity<Void> handleMatchCancel(@RequestBody Match match) {
        webhooksService.handleMatchCancelled(match);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/server-booting")
    public ResponseEntity<Void> handleServerBooting(@RequestBody Match match) {
        webhooksService.handleBootingServer(match);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/players-ready")
    public ResponseEntity<Void> handleAllPLayersReady(@RequestBody Match match) {
        webhooksService.handleAllPlayersConnected(match);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/map-loading")
    public ResponseEntity<Void> handleMapLoading(@RequestBody Match match) {
        webhooksService.handleServerReady(match);
        return ResponseEntity.ok().build();
    }
}
