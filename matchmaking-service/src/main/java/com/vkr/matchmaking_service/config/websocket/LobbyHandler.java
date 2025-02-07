package com.vkr.matchmaking_service.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class LobbyHandler extends TextWebSocketHandler {

    private static final int MAX_PLAYERS = 10; // Максимальное количество игроков в лобби
    private final List<String> team1 = new ArrayList<>();
    private final List<String> team2 = new ArrayList<>();
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        session.sendMessage(new TextMessage("Вы подключены к лобби."));
        broadcastUpdate();
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, String> action = new ObjectMapper().readValue(payload, Map.class);

        String playerName = action.get("playerName");
        String playerAction = action.get("action");

        switch (playerAction) {
            case "join":
                handleJoin(playerName, action.get("team"), session);
                break;
            case "leave":
                handleLeave(playerName, session);
                break;
            default:
                session.sendMessage(new TextMessage("Неизвестное действие: " + playerAction));
        }
    }

    private void handleJoin(String playerName, String team, WebSocketSession session) throws Exception {
        if (playerName == null || playerName.trim().isEmpty()) {
            session.sendMessage(new TextMessage("Ошибка: имя игрока не может быть пустым."));
            return;
        }

        synchronized (this) {
            if (team.equals("team1")) {
                if (!team1.contains(playerName) && team1.size() < MAX_PLAYERS / 2) {
                    team1.add(playerName);
                    session.sendMessage(new TextMessage("Вы добавлены в команду 1."));
                } else {
                    session.sendMessage(new TextMessage("Команда 1 заполнена или игрок уже в лобби."));
                    return;
                }
            } else if (team.equals("team2")) {
                if (!team2.contains(playerName) && team2.size() < MAX_PLAYERS / 2) {
                    team2.add(playerName);
                    session.sendMessage(new TextMessage("Вы добавлены в команду 2."));
                } else {
                    session.sendMessage(new TextMessage("Команда 2 заполнена или игрок уже в лобби."));
                    return;
                }
            } else {
                session.sendMessage(new TextMessage("Ошибка: выбрана неверная команда."));
                return;
            }
        }

        broadcastUpdate();
    }

    private void handleLeave(String playerName, WebSocketSession session) throws Exception {
        if (playerName == null || playerName.trim().isEmpty()) {
            session.sendMessage(new TextMessage("Ошибка: имя игрока не указано."));
            return;
        }

        synchronized (this) {
            boolean removed = team1.remove(playerName) || team2.remove(playerName);
            if (removed) {
                session.sendMessage(new TextMessage("Вы вышли из лобби."));
            } else {
                session.sendMessage(new TextMessage("Вы не находитесь в лобби."));
            }
        }

        broadcastUpdate();
    }

    private void broadcastUpdate() {
        String updateMessage = "Команда 1: " + team1 + "\nКоманда 2: " + team2;

        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(updateMessage));
            } catch (Exception e) {
                System.err.println("Ошибка отправки сообщения: " + e.getMessage());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
        // Очистка игрока из команд при разрыве соединения
        synchronized (this) {
            team1.removeIf(player -> session.isOpen());
            team2.removeIf(player -> session.isOpen());
        }
        broadcastUpdate();
    }
}

