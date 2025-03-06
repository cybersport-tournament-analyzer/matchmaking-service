package com.vkr.matchmaking_service.kafka.consumer;

import com.vkr.matchmaking_service.entity.lobby.Lobby;
import com.vkr.matchmaking_service.exception.KafkaConsumerException;
import com.vkr.matchmaking_service.kafka.event.lobbyStart.LobbyStartEvent;
import com.vkr.matchmaking_service.service.lobby.LobbyService;
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
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.lobby-start.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(LobbyStartEvent event, Acknowledgment ack) {
        try {
            System.out.println("зашел 2");
            log.info("Consumed lobby start event: {}", event);
            lobbyService.createLobby(event.getMode(), event.getFormat(), "76561198258376387", event.getTournamentMatchId());
        } catch (Exception e) {
            throw new KafkaConsumerException(e);

        }
    }
}
