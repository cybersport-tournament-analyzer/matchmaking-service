package com.vkr.matchmaking_service.kafka.producer;

import com.vkr.matchmaking_service.kafka.event.KafkaEvent;

public interface KafkaProducer<T extends KafkaEvent> {
    void produce(T event);

    void produce(T event, Runnable runnable);
}
