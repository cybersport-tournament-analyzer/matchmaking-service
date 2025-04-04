package com.vkr.matchmaking_service.service.server;

import com.vkr.matchmaking_service.dto.match.MatchPlayerDto;
import com.vkr.matchmaking_service.dto.match.MatchStartingDto;
import com.vkr.matchmaking_service.dto.match.StartMatchPlayerDto;
import com.vkr.matchmaking_service.dto.server.ServerSettingsDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Path;

public interface ServerService {

    Server getAvailableServer() throws IOException, InterruptedException;

    Page<Server> getAllServers(Pageable pageable) throws IOException, InterruptedException;

    void startServer(String serverId) throws IOException, InterruptedException;

    void stopServer(String serverId) throws IOException, InterruptedException;

    void uploadFileToServer(String serverId, String filePath, Path localFilePath) throws IOException, InterruptedException;

    void deleteFileFromServer(String serverId, String filePath) throws IOException, InterruptedException;

    Server getServerById(String serverId) throws IOException, InterruptedException;

    String getServerIp(String serverId) throws IOException, InterruptedException;

    Match startMatch(MatchStartingDto matchStartingDto) throws IOException, InterruptedException;

    Match getMatchById(String matchId) throws IOException, InterruptedException;

    Match stopMatch(String matchId) throws IOException, InterruptedException;

    MatchPlayerDto addPlayerToMatch(String matchId, StartMatchPlayerDto startMatchPlayerDto) throws IOException, InterruptedException;

    void updateServer(ServerSettingsDto serverSettingsDto) throws IOException, InterruptedException;
}
