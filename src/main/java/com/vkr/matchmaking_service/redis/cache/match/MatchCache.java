package com.vkr.matchmaking_service.redis.cache.match;


import com.vkr.matchmaking_service.dto.match.StartMatchPlayerDto;
import com.vkr.matchmaking_service.dto.tournament_client.player.PlayerDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.redis.cache.series.SeriesCache;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "matches")
@Jacksonized
public class MatchCache implements Serializable {

    @Id
    private String id;

    private UUID tournamentId;

    @Indexed
    private UUID tournamentMatchId;

    @Indexed
    private int seriesOrder;

    private String mode;

    private String format;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Duration duration;

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

    private Match match;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Kda {
        private int kills;
        private int deaths;
        private int assists;
    }

}
