package com.vkr.matchmaking_service.exception.dao;

public class MatchNotFoundException extends RuntimeException{
    public MatchNotFoundException(String message) {
        super(message);
    }
}