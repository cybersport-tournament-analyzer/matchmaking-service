package com.vkr.matchmaking_service.exception;

public class LobbyNotFoundException extends RuntimeException{
    public LobbyNotFoundException(String message) {
        super(message);
    }
}
