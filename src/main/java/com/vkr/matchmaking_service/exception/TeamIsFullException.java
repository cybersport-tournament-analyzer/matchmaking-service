package com.vkr.matchmaking_service.exception;

public class TeamIsFullException extends RuntimeException{
    public TeamIsFullException(String message) {
        super(message);
    }
}
