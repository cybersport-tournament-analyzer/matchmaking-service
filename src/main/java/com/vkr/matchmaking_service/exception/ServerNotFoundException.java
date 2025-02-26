package com.vkr.matchmaking_service.exception;

public class ServerNotFoundException extends RuntimeException{
    public ServerNotFoundException(String message) {
        super(message);
    }
}
