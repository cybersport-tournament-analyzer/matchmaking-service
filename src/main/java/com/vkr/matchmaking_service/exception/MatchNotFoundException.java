package com.vkr.matchmaking_service.exception;

public class MatchNotFoundException extends RuntimeException{
    public MatchNotFoundException(String message) {
        super(message);
    }
}