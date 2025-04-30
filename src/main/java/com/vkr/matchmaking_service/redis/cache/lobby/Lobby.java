package com.vkr.matchmaking_service.redis.cache.lobby;

import com.vkr.matchmaking_service.dto.match.StartMatchPlayerDto;
import com.vkr.matchmaking_service.dto.tournament_client.player.PlayerDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.pickbans.PickBanSession;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "lobby", timeToLive = 10000)
@Jacksonized
public class Lobby implements Serializable {

    @Id
    private UUID id;

    private UUID tournamentId;

    private String mode;

    private PickBanSession pickBanSession;

    private String format;
    private String link;

    private StartMatchPlayerDto admin;

    private Map<Integer, PlayerDto> team1 = new HashMap<>();
    private Map<Integer, PlayerDto> team2 = new HashMap<>();

    private int team1Score;
    private int team2Score;

    private String team1Name;
    private String team2Name;

    private String team1flag;
    private String team2flag;

    private int currentMapNumber;

    private List<Match> matches = new ArrayList<>();

    public int maxPlayersPerTeam() {
        return switch (mode) {
            case "1vs1" -> 1;
            case "2vs2" -> 2;
            default -> 5;
        };
    }

    public boolean full() {
        return team1.size() + team2.size() >= maxPlayersPerTeam() * 2;
    }

    public void setReady(String steamId, boolean ready) {
        team1.values().stream().filter(p -> p.getPlayerSteamId().equals(steamId)).findFirst().ifPresent(p -> p.setReady(ready));
        team2.values().stream().filter(p -> p.getPlayerSteamId().equals(steamId)).findFirst().ifPresent(p -> p.setReady(ready));
    }

}

