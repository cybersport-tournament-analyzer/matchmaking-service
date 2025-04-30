package com.vkr.matchmaking_service.service.server;

import com.vkr.matchmaking_service.dto.match.MatchPlayerDto;
import com.vkr.matchmaking_service.dto.match.MatchStartingDto;
import com.vkr.matchmaking_service.dto.match.StartMatchPlayerDto;
import com.vkr.matchmaking_service.dto.server.ServerMetricsDto;
import com.vkr.matchmaking_service.dto.server.ServerSettingsDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncServerService {

    CompletableFuture<Server> getAvailableServer() throws IOException, InterruptedException;

    CompletableFuture<PageImpl<Server>> getAllServers(Pageable pageable) throws IOException, InterruptedException;

    CompletableFuture<Void> startServer(String serverId) throws IOException, InterruptedException;

    CompletableFuture<Void> stopServer(String serverId) throws IOException, InterruptedException;

    CompletableFuture<Void> uploadFileToServer(String serverId, String filePath, Path localFilePath) throws IOException, InterruptedException;

    CompletableFuture<Void> deleteFileFromServer(String serverId, String filePath) throws IOException, InterruptedException;

    CompletableFuture<Server> getServerById(String serverId) throws IOException, InterruptedException;

    CompletableFuture<String> getServerIp(String serverId) throws IOException, InterruptedException;

    CompletableFuture<Match> startMatch(MatchStartingDto matchStartingDto) throws IOException, InterruptedException;

    CompletableFuture<Match> stopMatch(String matchId) throws IOException, InterruptedException;

    CompletableFuture<Void> updateServer(ServerSettingsDto serverSettingsDto) throws IOException, InterruptedException;

    CompletableFuture<List<String>> getConsoleLogs(String serverId, int maxLines) throws IOException, InterruptedException;
}
