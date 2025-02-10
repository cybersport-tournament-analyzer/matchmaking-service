package com.vkr.matchmaking_service.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vkr.matchmaking_service.entity.server.Lobby;
import com.vkr.matchmaking_service.service.lobby.LobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LobbyHandler extends TextWebSocketHandler {

    private final LobbyService lobbyService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<WebSocketSession, UUID> sessionLobbyMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage("Вы подключены к серверу лобби."));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, String> data = objectMapper.readValue(message.getPayload(), Map.class);
        String action = data.get("action");
        String steamId = data.get("steamId");

        switch (action) {
            case "create":
                String mode = data.get("mode");
                UUID lobbyId = lobbyService.createLobby(mode, steamId).getId();
                sessionLobbyMap.put(session, lobbyId);
                session.sendMessage(new TextMessage("Лобби создано: " + lobbyId));
                break;

            case "join":
                UUID joinLobbyId = UUID.fromString(data.get("lobbyId"));
                String team = data.get("team");
                lobbyService.addPlayer(joinLobbyId, steamId, team);
                sessionLobbyMap.put(session, joinLobbyId);
                broadcastLobbyUpdate(joinLobbyId);
                break;

//            case "switch":
//                UUID switchLobbyId = sessionLobbyMap.get(session);
//                if (switchLobbyId != null) {
//                    lobbyService.switchTeam(switchLobbyId, steamId);
//                    broadcastLobbyUpdate(switchLobbyId);
//                }
//                break;

            case "leave":
                UUID leaveLobbyId = sessionLobbyMap.remove(session);
                if (leaveLobbyId != null) {
                    lobbyService.removePlayer(leaveLobbyId, steamId);
                    broadcastLobbyUpdate(leaveLobbyId);
                }
                break;
        }
    }

    private void broadcastLobbyUpdate(UUID lobbyId) throws IOException {
        Lobby lobby = lobbyService.getLobbyById(String.valueOf(lobbyId));
        String updateMessage = objectMapper.writeValueAsString(lobby);
        for (WebSocketSession session : sessionLobbyMap.keySet()) {
            if (sessionLobbyMap.get(session).equals(lobbyId)) {
                session.sendMessage(new TextMessage(updateMessage));
            }
        }
    }
}
