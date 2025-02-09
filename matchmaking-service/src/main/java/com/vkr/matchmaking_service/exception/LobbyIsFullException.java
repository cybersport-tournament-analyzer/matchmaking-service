package com.vkr.matchmaking_service.exception;

public class LobbyIsFullException extends RuntimeException{
    public LobbyIsFullException(String message) {
        super(message);
    }
}
