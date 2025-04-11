package com.vkr.matchmaking_service.kafka.event.roundEnd;

import com.vkr.matchmaking_service.dto.stats.KillEventDto;
import com.vkr.matchmaking_service.dto.stats.RoundEndReasonDto;
import com.vkr.matchmaking_service.dto.stats.RoundStatsDto;
import com.vkr.matchmaking_service.kafka.event.KafkaEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class RoundEndEvent implements KafkaEvent {
    private RoundStatsDto roundStats;
    private RoundEndReasonDto roundEndReason;
    private List<KillEventDto> killEvents;

}
