package com.vkr.matchmaking_service.config.kafka;

import com.vkr.matchmaking_service.entity.match.Match;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerConfig {
    private final KafkaTemplate<String, Match> kafkaTemplate;

    public KafkaProducerConfig(KafkaTemplate<String, Match> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMatchFinished(Match event) {
        kafkaTemplate.send("match-finished-topic", event);
    }
}
