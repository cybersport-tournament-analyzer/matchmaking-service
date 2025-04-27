package com.vkr.matchmaking_service.redis.cache.series;


import com.vkr.matchmaking_service.entity.pickbans.PickBanSession;
import com.vkr.matchmaking_service.redis.cache.match.MatchCache;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "series")
@Jacksonized
public class SeriesCache {

    @Id
    private UUID id;

    private UUID tournamentId;

    private UUID tournamentMatchId;

    private PickBanSession pickBanSession;

    private LocalDateTime dateTime;

//    private String status;

    private List<MatchCache> matches = new ArrayList<>();
}
