package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.entity.match.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/event")
    public ResponseEntity<Void> handleWebhook(@RequestBody Match match) {
        messagingTemplate.convertAndSend("/topic/match", match);
        return ResponseEntity.ok().build();
    }
}

