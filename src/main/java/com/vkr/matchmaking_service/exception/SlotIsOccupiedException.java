package com.vkr.matchmaking_service.exception;

public class SlotIsOccupiedException extends RuntimeException{
    public SlotIsOccupiedException(String message) {
        super(message);
    }
}
