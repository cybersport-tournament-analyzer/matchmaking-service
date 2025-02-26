package com.vkr.matchmaking_service.exception;

public class WrongInputException extends IllegalArgumentException{
    public WrongInputException(String s) {
        super(s);
    }
}
