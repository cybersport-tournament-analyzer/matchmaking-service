package com.vkr.matchmaking_service.kafka.event.roundEnd;

import com.vkr.matchmaking_service.dto.stats.KillEventDto;
import com.vkr.matchmaking_service.dto.stats.RoundEndReasonDto;
import com.vkr.matchmaking_service.dto.stats.RoundStatsDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.kafka.event.KafkaEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class RoundEndEvent implements KafkaEvent {
    private UUID tournamentMatchId;
    private UUID tournamentId;
    private RoundStatsDto roundStats;
    private RoundEndReasonDto roundEndReason;
    private List<KillEventDto> killEvents;
    private Match match;
    private int isFinal;
}
