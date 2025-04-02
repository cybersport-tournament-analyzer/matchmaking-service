package com.vkr.matchmaking_service.redis.cache.lobby;

import com.vkr.matchmaking_service.dto.user.UserDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.pickbans.PickBanSession;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "lobby", timeToLive = 3600) //1hour
@Jacksonized
public class Lobby {

    @Id
    private UUID id;

//    private UUID tournamentMatchId;

    private String mode;

    private PickBanSession pickBanSession;

    private String format;
    private String link;

    private Map<Integer, UserDto> team1 = new HashMap<>();
    private Map<Integer, UserDto> team2 = new HashMap<>();

    private int team1Score;
    private int team2Score;

    private String team1Name;
    private String team2Name;

    private int currentMapNumber;

    private List<Match> matches = new ArrayList<>();

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

    public void setReady(String steamId, boolean ready) {
        team1.values().stream().filter(p -> p.getSteamId().equals(steamId)).findFirst().ifPresent(p -> p.setReady(ready));
        team2.values().stream().filter(p -> p.getSteamId().equals(steamId)).findFirst().ifPresent(p -> p.setReady(ready));
    }

}

