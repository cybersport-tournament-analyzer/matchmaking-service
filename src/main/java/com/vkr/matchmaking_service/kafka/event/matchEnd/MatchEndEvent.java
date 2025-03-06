package com.vkr.matchmaking_service.kafka.event.matchEnd;

import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.kafka.event.KafkaEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class MatchEndEvent implements KafkaEvent {
    private UUID tournamentMatchId;
    private int team1Score;
    private int team2Score;
    private Match match;
}
