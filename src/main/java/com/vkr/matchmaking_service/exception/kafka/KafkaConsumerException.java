package com.vkr.matchmaking_service.exception.kafka;

public class KafkaConsumerException extends RuntimeException {
    public KafkaConsumerException(Throwable e) {
        super(e);
    }
}
