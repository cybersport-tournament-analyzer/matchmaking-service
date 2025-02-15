package com.vkr.matchmaking_service.service.lobby;

import com.vkr.matchmaking_service.client.UserServiceClient;
import com.vkr.matchmaking_service.dto.user.UserDto;
import com.vkr.matchmaking_service.entity.lobby.Lobby;
import com.vkr.matchmaking_service.exception.LobbyIsFullException;
import com.vkr.matchmaking_service.exception.LobbyNotFoundException;
import com.vkr.matchmaking_service.exception.TeamIsFullException;
import com.vkr.matchmaking_service.exception.WrongInputException;
import com.vkr.matchmaking_service.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LobbyServiceImpl implements LobbyService {

    private final UserServiceClient userServiceClient;
    private final JedisPool jedisPool; // Используем JedisPool вместо RedisTemplate
    private static final String LOBBY_KEY_PREFIX = "lobby:";

    @Override
    public List<Lobby> getAllLobbies() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys(LOBBY_KEY_PREFIX + "*");
            if (keys == null || keys.isEmpty()) return Collections.emptyList();

            List<Lobby> lobbies = new ArrayList<>();
            for (String key : keys) {
                String json = jedis.get(key);
                if (json != null) {
                    lobbies.add(JsonUtils.fromJson(json, Lobby.class));
                }
            }
            return lobbies;
        }
    }


    @Override
    public Lobby createLobby(String mode, String steamId) {
        if (!List.of("1x1", "2x2", "5x5").contains(mode)) {
            throw new WrongInputException("Wrong game mode!");
        }

        UserDto creator = userServiceClient.getUserBySteamId(steamId);
        Lobby lobby = new Lobby(UUID.randomUUID(), mode, new ArrayList<>(), new ArrayList<>());
        lobby.getTeam1().add(creator);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(LOBBY_KEY_PREFIX + lobby.getId(), 120, JsonUtils.toJson(lobby));
        }

        return lobby;
    }

    @Override
    public void addPlayer(UUID lobbyId, String steamId, String team) {
        String key = LOBBY_KEY_PREFIX + lobbyId;

        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            if (json == null) throw new LobbyNotFoundException("Lobby not found!");

            Lobby lobby = JsonUtils.fromJson(json, Lobby.class);
            if (lobby.full()) throw new LobbyIsFullException("Lobby is full!");

            List<UserDto> targetTeam = "team1".equals(team) ? lobby.getTeam1() : lobby.getTeam2();
            if (targetTeam.size() >= lobby.maxPlayersPerTeam()) {
                throw new TeamIsFullException("Chosen team is full!");
            }

            UserDto currentPlayer = userServiceClient.getUserBySteamId(steamId);
            targetTeam.add(currentPlayer);

            jedis.setex(key, 120, JsonUtils.toJson(lobby));
        }
    }

    @Override
    public void removePlayer(UUID lobbyId, String steamId) {
        String key = LOBBY_KEY_PREFIX + lobbyId;

        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            if (json == null) throw new LobbyNotFoundException("Lobby not found!");

            Lobby lobby = JsonUtils.fromJson(json, Lobby.class);
            lobby.getTeam1().removeIf(player -> player.getSteamId().equals(steamId));
            lobby.getTeam2().removeIf(player -> player.getSteamId().equals(steamId));

            if (lobby.getTeam1().isEmpty() && lobby.getTeam2().isEmpty()) {
                jedis.del(key);
            } else {
                jedis.setex(key, 120, JsonUtils.toJson(lobby));
            }
        }
    }

    @Override
    public Lobby getLobbyById(String lobbyId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(LOBBY_KEY_PREFIX + lobbyId);
            if (json == null) throw new LobbyNotFoundException("Lobby not found!");
            return JsonUtils.fromJson(json, Lobby.class);
        }
    }

    @Override
    public void setReady(UUID lobbyId, String steamId, boolean ready) {
        String key = LOBBY_KEY_PREFIX + lobbyId;

        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            if (json == null) throw new LobbyNotFoundException("Lobby not found!");

            Lobby lobby = JsonUtils.fromJson(json, Lobby.class);
            lobby.setReady(steamId, ready);

            jedis.setex(key, 120, JsonUtils.toJson(lobby));
        }
    }

}

