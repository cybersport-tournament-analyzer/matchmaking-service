package com.vkr.matchmaking_service.exception.dao;

public class WrongInputException extends IllegalArgumentException{
    public WrongInputException(String s) {
        super(s);
    }
}
