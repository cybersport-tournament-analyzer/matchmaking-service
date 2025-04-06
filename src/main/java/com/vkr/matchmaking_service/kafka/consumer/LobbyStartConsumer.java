package com.vkr.matchmaking_service.kafka.consumer;

import com.vkr.matchmaking_service.exception.KafkaConsumerException;
import com.vkr.matchmaking_service.exception.LobbyNotFoundException;
import com.vkr.matchmaking_service.kafka.event.lobbyStart.LobbyStartEvent;
import com.vkr.matchmaking_service.redis.service.lobby.LobbyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
@RequiredArgsConstructor
public class LobbyStartConsumer implements KafkaConsumer<LobbyStartEvent> {

    private final LobbyService lobbyService;

    @Override
    @Transactional
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.lobby-start.name}", groupId = "${spring.data.kafka.group-id}",
    containerFactory = "kafkaListenerContainerFactory")
    public void consume(LobbyStartEvent event, Acknowledgment ack) {
        try {
            log.info("Consumed lobby event: {}", event);
            lobbyService.createLobby(event.getMode(), event.getFormat(), event.getTeam1(), event.getTeam2(), event.getTournamentMatchId());
            ack.acknowledge();
        } catch (LobbyNotFoundException e) {
            log.warn("Lobby not found, will retry: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in consumer: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }
}
