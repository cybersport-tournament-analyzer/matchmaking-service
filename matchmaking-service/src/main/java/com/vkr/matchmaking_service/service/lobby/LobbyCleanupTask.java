package com.vkr.matchmaking_service.service.lobby;

import com.vkr.matchmaking_service.entity.lobby.Lobby;
import com.vkr.matchmaking_service.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LobbyCleanupTask {

    private final JedisPool jedisPool;

    @Scheduled(fixedRate = 300_000) // Запускается каждые 5 минут
    public void cleanOldLobbies() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("lobby:*");
            if (keys == null) return;

            LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
            for (String key : keys) {
                String json = jedis.get(key);
                if (json != null) {
                    Lobby lobby = JsonUtils.fromJson(json, Lobby.class);
                    if (lobby.getCreatedAt().isBefore(tenMinutesAgo)) {
                        jedis.del(key);
                    }
                }
            }
        }
    }
}


