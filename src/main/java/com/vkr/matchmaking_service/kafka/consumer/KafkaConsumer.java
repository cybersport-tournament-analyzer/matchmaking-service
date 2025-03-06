package com.vkr.matchmaking_service.kafka.consumer;

import com.vkr.matchmaking_service.kafka.event.KafkaEvent;
import org.springframework.kafka.support.Acknowledgment;

public interface KafkaConsumer<T extends KafkaEvent> {

    @SuppressWarnings("unused")
    void consume(T event, Acknowledgment ack);
}
