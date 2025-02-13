package com.vkr.matchmaking_service.entity.lobby;

import com.vkr.matchmaking_service.dto.user.UserDto;
import jakarta.persistence.Id;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "lobby", timeToLive = 120)
@Jacksonized
public class Lobby {

    @Id
    private UUID id;

    @Indexed
    private String mode;

    private List<UserDto> team1 = new ArrayList<>();
    private List<UserDto> team2 = new ArrayList<>();

    public int maxPlayersPerTeam() {
        return switch (mode) {
            case "1x1" -> 1;
            case "2x2" -> 2;
            default -> 5;
        };
    }

    public boolean full() {
        return team1.size() + team2.size() >= maxPlayersPerTeam() * 2;
    }
}

