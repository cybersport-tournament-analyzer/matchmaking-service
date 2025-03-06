package com.vkr.matchmaking_service.exception;

public class KafkaConsumerException extends RuntimeException {
    public KafkaConsumerException(Throwable e) {
        super(e);
    }
}
