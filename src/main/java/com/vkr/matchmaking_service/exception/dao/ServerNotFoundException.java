package com.vkr.matchmaking_service.exception.dao;

public class ServerNotFoundException extends RuntimeException{
    public ServerNotFoundException(String message) {
        super(message);
    }
}
