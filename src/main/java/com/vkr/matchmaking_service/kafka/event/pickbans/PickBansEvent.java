package com.vkr.matchmaking_service.kafka.event.pickbans;

import com.vkr.matchmaking_service.entity.pickbans.PickBanAction;
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
public class PickBansEvent implements KafkaEvent {
    private List<PickBanAction> pickbans;
    private String team1Name;
    private String team2Name;
    private UUID tournamentId;
}