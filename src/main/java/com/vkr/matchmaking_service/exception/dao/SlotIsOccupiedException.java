package com.vkr.matchmaking_service.exception.dao;

public class SlotIsOccupiedException extends RuntimeException{
    public SlotIsOccupiedException(String message) {
        super(message);
    }
}
