package com.vkr.matchmaking_service.redis.cache.series;


import com.vkr.matchmaking_service.dto.tournament_client.player.PlayerDto;
import com.vkr.matchmaking_service.entity.pickbans.PickBanSession;
import com.vkr.matchmaking_service.redis.cache.match.MatchCache;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "series")
@Jacksonized
public class SeriesCache implements Serializable {

    @Id
    private UUID tournamentMatchId;

    @Indexed
    private UUID tournamentId;

    private PickBanSession pickBanSession;

    private LocalDateTime dateTime;

    private String status;

    private Map<Integer, PlayerDto> team1 = new HashMap<>();
    private Map<Integer, PlayerDto> team2 = new HashMap<>();

    @Builder.Default
    private Map<Integer, Kda> team1Kda = new HashMap<>();

    @Builder.Default
    private Map<Integer, Kda> team2Kda = new HashMap<>();

    private int team1Score;
    private int team2Score;

    private String team1Name;
    private String team2Name;

    @Builder.Default
    private Map<Integer, MatchCache> matches = new HashMap<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Kda {
        private int kills = 0;
        private int deaths = 0;
        private int assists = 0;
    }
}
