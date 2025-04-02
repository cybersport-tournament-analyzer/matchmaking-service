package com.vkr.matchmaking_service.exception.dao;

public class LobbyIsFullException extends RuntimeException{
    public LobbyIsFullException(String message) {
        super(message);
    }
}
