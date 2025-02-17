package com.vkr.matchmaking_service.service.server;

import com.vkr.matchmaking_service.dto.match.MatchPlayerDto;
import com.vkr.matchmaking_service.dto.match.MatchStartingDto;
import com.vkr.matchmaking_service.dto.match.StartMatchPlayerDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface ServerService {

    Page<Server> getAllServers(Pageable pageable) throws IOException, InterruptedException;

    void startServer(String serverId) throws IOException, InterruptedException;

    void stopServer(String serverId) throws IOException, InterruptedException;

    Server getServerById(String serverId) throws IOException, InterruptedException;

    String getServerIp(String serverId) throws IOException, InterruptedException;

    Match startMatch(MatchStartingDto matchStartingDto) throws IOException, InterruptedException;

    Match getMatchById(String matchId) throws IOException, InterruptedException;

    Match stopMatch(String matchId) throws IOException, InterruptedException;

    MatchPlayerDto addPlayerToMatch(String matchId, StartMatchPlayerDto startMatchPlayerDto) throws IOException, InterruptedException;
}
