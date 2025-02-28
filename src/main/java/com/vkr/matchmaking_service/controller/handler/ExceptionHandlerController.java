package com.vkr.matchmaking_service.controller.handler;

import com.vkr.matchmaking_service.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(ServerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleServerNotFoundException(ServerNotFoundException e, HttpServletRequest request) {
        log.error("Server not found: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(LobbyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleLobbyNotFoundException(LobbyNotFoundException e, HttpServletRequest request) {
        log.error("Lobby not found: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(LobbyIsFullException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleLobbyIsFullException(LobbyIsFullException e, HttpServletRequest request) {
        log.error("Lobby is full: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(TeamIsFullException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTeamIsFullException(TeamIsFullException e, HttpServletRequest request) {
        log.error("Team is full: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(WrongInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleWrongInputException(WrongInputException e, HttpServletRequest request) {
        log.error("Wrong input format: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(MatchNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMatchNotFoundException(MatchNotFoundException e, HttpServletRequest request) {
        log.error("Match not found: {}", e.getMessage());
        return buildErrorResponse(e, request);
    }

    private ErrorResponse buildErrorResponse(Exception e, HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(request.getRequestURI())
                .message(e.getMessage())
                .build();
    }
}
